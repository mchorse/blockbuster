package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;

public class GuiMorphActionPanel extends GuiActionPanel<MorphAction>
{
    public GuiNestedEdit pickMorph;

    public GuiMorphActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);

        this.pickMorph = new GuiNestedEdit(mc, (editing) -> ClientProxy.panels.addMorphs(this, editing, this.action.morph));
        this.pickMorph.flex().relative(this.area).set(0, 5, 80, 20).x(0.5F, -30);

        this.add(this.pickMorph);
    }

    @Override
    public void setMorph(AbstractMorph morph)
    {
        this.action.morph = morph;
        this.pickMorph.setMorph(action.morph);
    }

    @Override
    public void fill(MorphAction action)
    {
        super.fill(action);

        ClientProxy.panels.morphs.removeFromParent();
        this.pickMorph.setMorph(action.morph);
    }

    @Override
    public void disappear()
    {
        ClientProxy.panels.morphs.finish();
        ClientProxy.panels.morphs.removeFromParent();

        super.disappear();
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.action.morph != null)
        {
            int x = this.area.mx();
            int y = this.area.y(0.8F);

            GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
            this.action.morph.renderOnScreen(this.mc.player, x, y, this.area.h / 3F, 1.0F);
            GuiDraw.unscissor(context);
        }

        super.draw(context);
    }
}