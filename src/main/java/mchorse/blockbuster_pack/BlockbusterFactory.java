package mchorse.blockbuster_pack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelHandler.ModelCell;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructureListRequest;
import mchorse.blockbuster_pack.client.gui.GuiCustomMorph;
import mchorse.blockbuster_pack.client.gui.GuiImageMorph;
import mchorse.blockbuster_pack.client.gui.GuiParticleMorph;
import mchorse.blockbuster_pack.client.gui.GuiRecordMorph;
import mchorse.blockbuster_pack.client.gui.GuiSequencerMorph;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph.SequenceEntry;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blockbuster morph factory
 *
 * This factory is responsible for adding all custom modeled morphs provided by
 * a user (in his config folder), the server (in world save's blockbuster
 * folder) or added by API (steve, alex and fred).
 */
public class BlockbusterFactory implements IMorphFactory
{
    public ModelHandler models;

    @Override
    public void register(MorphManager manager)
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {}

    @Override
    public void registerMorphEditors(List<GuiAbstractMorph> editors)
    {
        Minecraft mc = Minecraft.getMinecraft();

        editors.add(new GuiCustomMorph(mc));
        editors.add(new GuiImageMorph(mc));
        editors.add(new GuiSequencerMorph(mc));
        editors.add(new GuiRecordMorph(mc));
        editors.add(new GuiParticleMorph(mc));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(AbstractMorph morph)
    {
        if (morph instanceof ImageMorph)
        {
            return I18n.format("blockbuster.morph.image");
        }
        else if (morph instanceof SequencerMorph)
        {
            return I18n.format("blockbuster.morph.sequencer");
        }
        else if (morph instanceof RecordMorph)
        {
            return I18n.format("blockbuster.morph.record");
        }
        else if (morph instanceof StructureMorph)
        {
            return I18n.format("blockbuster.morph.structure");
        }
        else if (morph instanceof StructureMorph)
        {
            return I18n.format("blockbuster.morph.structure");
        }
        else if (morph instanceof ParticleMorph)
        {
            return I18n.format("blockbuster.morph.particle");
        }

        String[] splits = morph.name.split("\\.");

        if (splits.length >= 2 && splits[0].equals("blockbuster") && this.models.models.containsKey(splits[1]))
        {
            String name = this.models.models.get(splits[1]).model.name;

            return name.isEmpty() ? splits[1] : name;
        }

        return null;
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        String name = tag.getString("Name");
        AbstractMorph morph = null;
        name = name.substring(name.indexOf(".") + 1);

        /* Utility */
        if (name.equals("image"))
        {
            morph = new ImageMorph();
        }
        else if (name.equals("sequencer"))
        {
            morph = new SequencerMorph();
        }
        else if (name.equals("record"))
        {
            morph = new RecordMorph();
        }
        else if (name.equals("structure"))
        {
            morph = new StructureMorph();
        }
        else if (name.equals("particle"))
        {
            morph = new ParticleMorph();
        }
        else
        {
            /* Custom model morphs */
            CustomMorph custom = new CustomMorph();
            ModelCell entry = this.models.models.get(name);

            if (entry != null)
            {
                custom.model = entry.model;
            }

            morph = custom;
        }

        morph.fromNBT(tag);

        return morph;
    }

    @Override
    public void getMorphs(MorphList morphs, World world)
    {
        /* Custom model morphs */
        for (Map.Entry<String, ModelCell> entry : this.models.models.entrySet())
        {
            String key = entry.getKey();
            CustomMorph original = new CustomMorph();
            String category = "blockbuster";
            String variant = key.contains("/") ? key.substring(0, key.lastIndexOf("/")) : "";

            original.name = "blockbuster." + key;
            original.model = entry.getValue().model;

            if (key.equals("yike") || original.model == null)
            {
                continue;
            }

            /* Morphs with default texture */
            if (original.model.defaultTexture != null || original.model.providesMtl)
            {
                CustomMorph actor = (CustomMorph) original.clone(world.isRemote);

                morphs.addMorphVariant(actor.name, category, variant, "", actor);
            }

            /* Morphs with skins */
            List<String> skins = new ArrayList<String>();

            for (String str : this.models.pack.getSkins(original.model.skins))
            {
                skins.add(original.model.skins + "/skins/" + str + ".png");
            }

            for (String str : this.models.pack.getSkins(key))
            {
                skins.add(key + "/skins/" + str + ".png");
            }

            for (String skin : skins)
            {
                CustomMorph actor = (CustomMorph) original.clone(world.isRemote);

                actor.skin = RLUtils.create("b.a", skin);
                morphs.addMorphVariant(actor.name, category, variant, skin, actor);
            }
        }

        /* Image morphs */
        for (String texture : this.models.pack.getSkins("image"))
        {
            ImageMorph image = new ImageMorph();

            image.texture = RLUtils.create("b.a", "image/skins/" + texture + ".png");
            morphs.addMorphVariant(image.name, "blockbuster_extra", texture, image);
        }

        /* Sequencer morphs */
        CustomMorph steve = new CustomMorph();
        steve.name = "blockbuster.fred";
        steve.model = this.models.models.get("fred").model;

        CustomMorph alex = new CustomMorph();
        alex.name = "blockbuster.alex";
        alex.model = this.models.models.get("alex").model;

        SequencerMorph steveAlex = new SequencerMorph();
        SequenceEntry steveEntry = new SequenceEntry(steve, 10);
        SequenceEntry alexEntry = new SequenceEntry(alex, 10);

        steveAlex.morphs.add(steveEntry);
        steveAlex.morphs.add(alexEntry);

        morphs.addMorphVariant("sequencer", "blockbuster_extra", "default", steveAlex);
        morphs.addMorphVariant("sequencer", "blockbuster_extra", "empty", new SequencerMorph());

        /* Record morph */
        morphs.addMorphVariant("record", "blockbuster_extra", "default", new RecordMorph());

        /* Structure morph */
        Dispatcher.sendToServer(new PacketStructureListRequest());

        for (String key : StructureMorph.STRUCTURES.keySet())
        {
            StructureMorph morph = new StructureMorph();

            morph.structure = key;
            morphs.addMorphVariant("structure", "blockbuster_extra", key, morph);
        }

        /* Particle morph */
        morphs.addMorph("particle", "blockbuster_extra", new ParticleMorph());
    }

    @Override
    public boolean hasMorph(String morph)
    {
        return morph.startsWith("blockbuster.") || morph.equals("sequencer") || morph.equals("structure") || morph.equals("particle");
    }
}