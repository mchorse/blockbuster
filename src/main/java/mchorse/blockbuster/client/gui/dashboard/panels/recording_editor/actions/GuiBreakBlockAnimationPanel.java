package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.recording.actions.BreakBlockAnimation;
import net.minecraft.client.Minecraft;

public class GuiBreakBlockAnimationPanel extends GuiBlockActionPanel<BreakBlockAnimation>
{
    public GuiTrackpadElement charge;

    public GuiBreakBlockAnimationPanel(Minecraft mc)
    {
        super(mc);

        this.title = "Break animation action";
        this.charge = new GuiTrackpadElement(mc, "Progress", (charge) -> this.action.progress = charge.intValue());
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
