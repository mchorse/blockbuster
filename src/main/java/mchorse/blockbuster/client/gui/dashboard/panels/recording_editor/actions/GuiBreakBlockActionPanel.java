package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.BreakBlockAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiBreakBlockActionPanel extends GuiBlockActionPanel<BreakBlockAction>
{
    public GuiToggleElement drop;

    public GuiBreakBlockActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);

        this.drop = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.record_editor.drop"), false, (b) -> this.action.drop = b.isToggled());
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