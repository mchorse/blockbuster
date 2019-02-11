package mchorse.blockbuster_pack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelHandler.ModelCell;
import mchorse.blockbuster_pack.client.gui.GuiCustomMorph;
import mchorse.blockbuster_pack.client.gui.GuiImageMorph;
import mchorse.blockbuster_pack.client.gui.GuiSequencerMorph;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
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
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(AbstractMorph morph)
    {
        if (morph instanceof SequencerMorph)
        {
            return I18n.format("blockbuster.morph.sequencer");
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
        name = name.substring(name.indexOf(".") + 1);

        if (name.equals("image"))
        {
            ImageMorph image = new ImageMorph();

            image.fromNBT(tag);

            return image;
        }

        if (name.equals("sequencer"))
        {
            SequencerMorph seq = new SequencerMorph();

            seq.fromNBT(tag);

            return seq;
        }

        CustomMorph morph = new CustomMorph();
        ModelCell entry = this.models.models.get(name);

        if (entry != null)
        {
            morph.model = entry.model;
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

                morphs.addMorphVariant(actor.name, "blockbuster", "", actor);
            }

            /* Morphs with skins */
            List<String> skins = new ArrayList<String>();

            for (String str : this.models.pack.getSkins(original.model.skins))
            {
                skins.add(original.model.skins + "/" + str);
            }

            for (String str : this.models.pack.getSkins(key))
            {
                skins.add(key + "/" + str);
            }

            for (String skin : skins)
            {
                CustomMorph actor = (CustomMorph) original.clone(world.isRemote);

                actor.skin = RLUtils.create("b.a", skin);
                morphs.addMorphVariant(actor.name, "blockbuster", skin, actor);
            }
        }

        /* Image morphs */
        for (String texture : this.models.pack.getSkins("image"))
        {
            ImageMorph image = new ImageMorph();

            image.texture = RLUtils.create("b.a", "image/" + texture);
            morphs.addMorphVariant(image.name, "blockbuster", texture, image);
        }

        /* Sequencer morph */
        morphs.addMorph("sequencer", "blockbuster", new SequencerMorph());
    }

    @Override
    public boolean hasMorph(String morph)
    {
        return morph.startsWith("blockbuster.") || morph.equals("sequencer");
    }
}