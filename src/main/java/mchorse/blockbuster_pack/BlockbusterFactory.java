package mchorse.blockbuster_pack;

import mchorse.blockbuster.api.ModelHandler;
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
import mchorse.blockbuster_pack.morphs.SnowstormMorph;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

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
    public BlockbusterSection section;

    @Override
    public void register(MorphManager manager)
    {
        manager.list.register(this.section = new BlockbusterSection("blockbuster"));
    }

    @Override
    public void registerMorphEditors(Minecraft mc, List<GuiAbstractMorph> editors)
    {
        editors.add(new GuiCustomMorph(mc));
        editors.add(new GuiImageMorph(mc));
        editors.add(new GuiSequencerMorph(mc));
        editors.add(new GuiRecordMorph(mc));
        editors.add(new GuiParticleMorph(mc));
        /* TODO: in the future...
           editors.add(new GuiSnowstormMorph(mc));
         */
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        String name = tag.getString("Name");
        AbstractMorph morph;
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
        else if (name.equals("snowstorm"))
        {
            morph = new SnowstormMorph();
        }
        else
        {
            /* Custom model morphs */
            CustomMorph custom = new CustomMorph();

            custom.model = this.models.models.get(name);
            morph = custom;
        }

        morph.fromNBT(tag);

        return morph;
    }

    @Override
    public boolean hasMorph(String morph)
    {
        return morph.startsWith("blockbuster.") || morph.equals("sequencer") || morph.equals("structure") || morph.equals("particle")  || morph.equals("snowstorm");
    }
}