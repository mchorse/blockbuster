package mchorse.blockbuster.events;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiActionPanel;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ActionPanelRegisterEvent extends Event
{
    public GuiRecordingEditorPanel panel;

    public ActionPanelRegisterEvent(GuiRecordingEditorPanel panel)
    {
        this.panel = panel;
    }

    public void register(Class<? extends Action> action, GuiActionPanel<? extends Action> panel)
    {
        this.panel.panels.put(action, panel);
    }
}