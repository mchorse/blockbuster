package mchorse.blockbuster_pack.client.gui;

import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.GuiBodyPartEditor;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;

public class GuiCustomBodyPartEditor extends GuiBodyPartEditor implements ILimbSelector
{
    public GuiCustomBodyPartEditor(Minecraft mc, GuiAbstractMorph editor)
    {
        super(mc, editor);
    }

    @Override
    protected void setPart(BodyPart part)
    {
        super.setPart(part);

        if (part != null)
        {
            GuiCustomMorph parent = (GuiCustomMorph) this.editor;

            parent.modelRenderer.limb = parent.morph.model.limbs.get(part.limb);
        }
    }

    @Override
    protected void pickLimb(String limbName)
    {
        GuiCustomMorph parent = (GuiCustomMorph) this.editor;

        super.pickLimb(limbName);
        parent.modelRenderer.limb = parent.morph.model.limbs.get(limbName);
    }

    @Override
    public void setLimb(String limb)
    {
        try
        {
            this.pickLimb(limb);
            this.limbs.setCurrent(limb);
        }
        catch (Exception e) {}
    }
}