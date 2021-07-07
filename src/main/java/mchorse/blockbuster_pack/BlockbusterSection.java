package mchorse.blockbuster_pack;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructureListRequest;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.blockbuster_pack.morphs.SnowstormMorph;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.blockbuster_pack.morphs.TrackerMorph;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.vanilla_pack.morphs.BlockMorph;
import mchorse.vanilla_pack.morphs.ItemMorph;
import mchorse.vanilla_pack.morphs.LabelMorph;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BlockbusterSection extends MorphSection
{
    public MorphCategory extra;
    public MorphCategory structures;
    public Map<String, MorphCategory> models = new HashMap<String, MorphCategory>();

    private boolean alex;
    private boolean steve;
    private SequencerMorph sequencer;

    public BlockbusterSection(String title)
    {
        super(title);

        this.extra = new MorphCategory(this, "blockbuster_extra");
        this.structures = new MorphCategory(this, "blockbuster_structures");

        /* Adding some default morphs which don't need to get reloaded */
        ImageMorph image = new ImageMorph();
        SnowstormMorph snow = new SnowstormMorph();
        SequencerMorph sequencer = new SequencerMorph();

        image.texture = RLUtils.create("blockbuster", "textures/gui/icon.png");
        snow.setScheme("default_rain");

        this.extra.add(image);
        this.extra.add(new ParticleMorph());
        this.extra.add(this.sequencer = sequencer);
        this.extra.add(new RecordMorph());
        this.extra.add(snow);

        /* By popular demand */
        this.extra.add(new ItemMorph());
        this.extra.add(new LabelMorph());
        this.extra.add(new BlockMorph());

        this.addFromNBT("{DisplayName:\"McHorse\",Skin:\"blockbuster:textures/entity/mchorse/skin.png\",BodyParts:[{Limb:\"head\",Morph:{Name:\"blockbuster.mchorse/head\"}}],Name:\"blockbuster.fred_3d\"}");
        
        this.extra.add(new TrackerMorph());
    }

    private void addFromNBT(String nbt)
    {
        try
        {
            NBTTagCompound tag = JsonToNBT.getTagFromJson(nbt);
            CustomMorph morph = new CustomMorph();

            morph.fromNBT(tag);
            this.extra.add(morph);
        }
        catch (Exception e)
        {}
    }

    public void addStructure(String name, boolean sort)
    {
        StructureMorph morph = new StructureMorph();

        morph.structure = name;
        this.structures.add(morph);

        if (sort)
        {
            this.structures.sort();
        }
    }

    public void addStructures(List<String> structures)
    {
        this.structures.clear();

        for (String name : structures)
        {
            this.addStructure(name, false);
        }

        this.structures.sort();
    }

    public void removeStructure(String name)
    {
        Iterator<AbstractMorph> it = this.structures.getMorphs().iterator();

        while (it.hasNext())
        {
            AbstractMorph morph = it.next();

            if (((StructureMorph) morph).structure.equals(name))
            {
                it.remove();
            }
        }
    }

    public void add(String key, Model model, boolean isRemote)
    {
        String path = this.getCategoryId(key);
        MorphCategory category = this.models.get(path);

        if (category == null)
        {
            category = new BlockbusterCategory(this, "blockbuster_models", path);
            this.models.put(path, category);
            this.categories.add(category);
        }

        for (AbstractMorph morph : category.getMorphs())
        {
            if (morph instanceof CustomMorph && ((CustomMorph) morph).getKey().equals(key))
            {
                return;
            }
        }

        CustomMorph morph = new CustomMorph();

        morph.name = "blockbuster." + key;
        morph.model = model;

        if (isRemote)
        {
            morph.skin = this.getSkin(key, model);
        }

        category.add(morph);
        category.sort();

        /* Really terrible hack to add sequences */
        this.alex = this.alex || key.equals("alex");
        this.steve = this.steve || key.equals("fred");

        if (this.steve && this.alex && this.sequencer.morphs.isEmpty())
        {
            CustomMorph alex = new CustomMorph();
            CustomMorph fred = new CustomMorph();

            alex.name = "blockbuster.alex";
            alex.updateModel(true);
            fred.name = "blockbuster.fred";
            fred.updateModel(true);

            this.sequencer.morphs.add(new SequencerMorph.SequenceEntry(alex));
            this.sequencer.morphs.add(new SequencerMorph.SequenceEntry(fred));
        }
    }

    /**
     * Get the first skin which can be found
     */
    @SideOnly(Side.CLIENT)
    private ResourceLocation getSkin(String key, Model model)
    {
        if (model.defaultTexture != null)
        {
            return null;
        }

        FolderEntry folder = ClientProxy.tree.getByPath(key + "/skins", null);

        if (folder != null)
        {
            for (AbstractEntry skinEntry : folder.getEntries())
            {
                if (skinEntry instanceof FileEntry)
                {
                    return ((FileEntry) skinEntry).resource;
                }
            }
        }

        folder = ClientProxy.tree.getByPath(model.skins + "/skins", null);

        if (folder != null)
        {
            for (AbstractEntry skinEntry : folder.getEntries())
            {
                if (skinEntry instanceof FileEntry)
                {
                    return  ((FileEntry) skinEntry).resource;
                }
            }
        }

        return null;
    }

    public void remove(String key)
    {
        String path = this.getCategoryId(key);
        String name = "blockbuster." + key;
        MorphCategory category = this.models.get(path);
        List<AbstractMorph> morphs = new ArrayList<AbstractMorph>();

        for (AbstractMorph m : category.getMorphs())
        {
            if (m.name.equals(name))
            {
                morphs.add(m);
            }
        }

        for (AbstractMorph morph : morphs)
        {
            category.remove(morph);
        }
    }

    private String getCategoryId(String key)
    {
        if (key.contains("/"))
        {
            key = FilenameUtils.getPath(key);

            return key.substring(0, key.length() - 1);
        }

        return "";
    }

    @Override
    public void update(World world)
    {
        /* Reload models and skin */
        Blockbuster.proxy.loadModels(false);
        Blockbuster.proxy.particles.reload();
        Dispatcher.sendToServer(new PacketStructureListRequest());

        this.categories.clear();
        this.add(this.extra);
        this.add(this.structures);

        /* Add models categories */
        for (MorphCategory category : this.models.values())
        {
            this.add(category);
        }
    }

    @Override
    public void reset()
    {
        this.structures.clear();
    }

    public static class BlockbusterCategory extends MorphCategory
    {
        public String subtitle;

        public BlockbusterCategory(MorphSection parent, String title, String subtitle)
        {
            super(parent, title);

            this.subtitle = subtitle;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTitle()
        {
            if (!this.subtitle.isEmpty())
            {
                return super.getTitle() + " (" + this.subtitle + ")";
            }

            return super.getTitle();
        }
    }
}