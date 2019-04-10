package mchorse.blockbuster_pack.client.gui;

import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.GuiBodyPartEditor;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;

public class GuiCustomBodyPartEditor extends GuiBodyPartEditor
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
    protected void pickLimb(String str)
    {
        GuiCustomMorph parent = (GuiCustomMorph) this.editor;

        super.pickLimb(str);
        parent.modelRenderer.limb = parent.morph.model.limbs.get(str);
    }
}