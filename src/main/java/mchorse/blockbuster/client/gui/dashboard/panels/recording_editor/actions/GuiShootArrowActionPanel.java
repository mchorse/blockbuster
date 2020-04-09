package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.ShootArrowAction;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiShootArrowActionPanel extends GuiActionPanel<ShootArrowAction>
{
    public GuiTrackpadElement charge;

    public GuiShootArrowActionPanel(Minecraft mc)
    {
        super(mc);

        this.charge = new GuiTrackpadElement(mc, (charge) -> this.action.charge = charge.intValue());
        this.charge.tooltip(I18n.format("blockbuster.gui.record_editor.arrow_charge"));
        this.charge.limit(0, 100, true);
        this.charge.flex().set(10, 0, 100, 20).relative(this.area).y(1, -30);

        this.add(this.charge);
    }

    @Override
    public void fill(ShootArrowAction action)
    {
        super.fill(action);

        this.charge.setValue(action.charge);
    }
}