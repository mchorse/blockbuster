package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import mchorse.blockbuster.recording.actions.CommandAction;
import net.minecraft.client.Minecraft;

public class GuiCommandActionPanel extends GuiActionPanel<CommandAction>
{
    public GuiTextElement command;

    public GuiCommandActionPanel(Minecraft mc)
    {
        super(mc);

        this.title = "Commnad action";
        this.command = new GuiTextElement(mc, 10000, (str) -> this.action.command = str);
        this.command.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -30).w(1, -20);

        this.children.add(this.command);
    }

    @Override
    public void fill(CommandAction action)
    {
        super.fill(action);

        this.command.setText(action.command);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);
    }
}