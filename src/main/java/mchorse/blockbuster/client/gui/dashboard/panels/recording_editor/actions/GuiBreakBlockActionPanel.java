package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.BreakBlockAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiBreakBlockActionPanel extends GuiBlockActionPanel<BreakBlockAction>
{
    public GuiToggleElement drop;

    public GuiBreakBlockActionPanel(Minecraft mc)
    {
        super(mc);

        this.drop = new GuiToggleElement(mc, I18n.format("blockbuster.gui.record_editor.drop"), false, (b) -> this.action.drop = b.isToggled());
        this.drop.flex().set(0, -16, 70, 11).relative(this.x.resizer());

        this.add(this.drop);
    }

    @Override
    public void fill(BreakBlockAction action)
    {
        super.fill(action);

        this.drop.toggled(action.drop);
    }
}