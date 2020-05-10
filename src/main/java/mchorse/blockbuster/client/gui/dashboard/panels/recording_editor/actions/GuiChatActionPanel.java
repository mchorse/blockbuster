package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.ChatAction;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import net.minecraft.client.Minecraft;

public class GuiChatActionPanel extends GuiActionPanel<ChatAction>
{
    public GuiTextElement command;

    public GuiChatActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);

        this.command = new GuiTextElement(mc, 10000, (str) -> this.action.message = str);
        this.command.flex().relative(this.area).set(10, 0, 0, 20).y(1, -30).w(1, -20);

        this.add(this.command);
    }

    @Override
    public void fill(ChatAction action)
    {
        super.fill(action);

        this.command.setText(action.message);
    }
}