package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.BreakBlockAnimation;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiBreakBlockAnimationPanel extends GuiBlockActionPanel<BreakBlockAnimation>
{
    public GuiTrackpadElement charge;

    public GuiBreakBlockAnimationPanel(Minecraft mc)
    {
        super(mc);

        this.charge = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.record_editor.progress"), (charge) -> this.action.progress = charge.intValue());
        this.charge.setLimit(0, 100, true);
        this.charge.resizer().set(0, -25, 100, 20).relative(this.x.resizer());

        this.children.add(this.charge);
    }

    @Override
    public void fill(BreakBlockAnimation action)
    {
        super.fill(action);

        this.charge.setValue(action.progress);
    }
}
