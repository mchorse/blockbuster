package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.MountingAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiMountingActionPanel extends GuiActionPanel<MountingAction>
{
    public GuiToggleElement mounting;

    public GuiMountingActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);

        this.mounting = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.record_editor.mounting"), false, (b) -> this.action.isMounting = b.isToggled());
        this.mounting.flex().set(10, 0, 60, 11).relative(this.area).y(1, -21);

        this.add(this.mounting);
    }

    @Override
    public void fill(MountingAction action)
    {
        super.fill(action);

        this.mounting.toggled(action.isMounting);
    }
}
