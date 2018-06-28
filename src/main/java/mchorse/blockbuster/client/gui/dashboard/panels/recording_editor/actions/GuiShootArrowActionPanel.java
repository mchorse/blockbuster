package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.recording.actions.ShootArrowAction;
import net.minecraft.client.Minecraft;

public class GuiShootArrowActionPanel extends GuiActionPanel<ShootArrowAction>
{
    public GuiTrackpadElement charge;

    public GuiShootArrowActionPanel(Minecraft mc)
    {
        super(mc);

        this.title = "Shoot arrow action";
        this.charge = new GuiTrackpadElement(mc, "Arrow charge", (charge) -> this.action.charge = charge.intValue());
        this.charge.setLimit(0, 100, true);
        this.charge.resizer().set(10, 0, 100, 20).parent(this.area).y(1, -30);

        this.children.add(this.charge);
    }

    @Override
    public void fill(ShootArrowAction action)
    {
        super.fill(action);

        this.charge.setValue(action.charge);
    }
}