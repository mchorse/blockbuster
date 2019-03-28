package mchorse.blockbuster_pack.client.gui;

import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.GuiBodyPartEditor;
import net.minecraft.client.Minecraft;

public class GuiCustomBodyPartEditor extends GuiBodyPartEditor
{
    public GuiCustomMorph parent;

    public GuiCustomBodyPartEditor(Minecraft mc, GuiCustomMorph parent)
    {
        super(mc);

        this.parent = parent;
    }

    @Override
    protected void setPart(BodyPart part)
    {
        super.setPart(part);

        if (part != null)
        {
            this.parent.modelRenderer.limb = this.parent.getMorph().model.limbs.get(part.limb);
        }
    }

    @Override
    protected void pickLimb(String str)
    {
        super.pickLimb(str);
        this.parent.modelRenderer.limb = this.parent.getMorph().model.limbs.get(str);
    }
}