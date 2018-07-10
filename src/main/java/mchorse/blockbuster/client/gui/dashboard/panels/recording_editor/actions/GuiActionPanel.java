package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.GuiTooltip;
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
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (!this.title.isEmpty())
        {
            this.font.drawStringWithShadow(this.title, this.area.x + 10, this.area.y + 10, 0xffffff);
        }
    }
}