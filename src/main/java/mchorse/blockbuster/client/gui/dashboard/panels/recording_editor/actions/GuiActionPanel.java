package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraft.client.Minecraft;

public abstract class GuiActionPanel<T extends Action> extends GuiElement
{
    public T action;
    public String title = "";

    public GuiActionPanel(Minecraft mc)
    {
        super(mc);
        this.createChildren();
    }

    public void fill(T action)
    {
        this.action = action;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        if (!this.title.isEmpty())
        {
            this.font.drawStringWithShadow(this.title, this.area.x + 6, this.area.y + 6, 0xffffff);
        }
    }
}