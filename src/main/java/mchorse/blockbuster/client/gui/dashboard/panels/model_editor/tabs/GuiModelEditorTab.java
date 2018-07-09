package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class GuiModelEditorTab extends GuiElement
{
    protected String title = "";
    protected GuiModelEditorPanel panel;

    public GuiModelEditorTab(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc);

        this.createChildren();
        this.panel = panel;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton) || this.isEnabled() && this.area.isInside(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);

        this.drawLabels();
        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    protected void drawLabels()
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 20, 0x88000000);

        this.font.drawStringWithShadow(this.title, this.area.x + 6, this.area.y + 6, 0xeeeeee);
    }
}