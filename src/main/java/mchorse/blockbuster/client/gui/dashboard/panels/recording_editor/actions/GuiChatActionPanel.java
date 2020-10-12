package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.ChatAction;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
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

    @Override
    public void draw(GuiContext context)
    {
        String message = this.action.getMessage(null);

        if (!message.isEmpty())
        {
            GuiDraw.drawTextBackground(this.font, message, this.command.area.x + 3, this.command.area.y - this.font.FONT_HEIGHT - 3, 0xffffff, 0x88000000);
        }

        super.draw(context);
    }
}