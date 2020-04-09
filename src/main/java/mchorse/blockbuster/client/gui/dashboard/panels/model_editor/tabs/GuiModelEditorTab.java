package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class GuiModelEditorTab extends GuiElement
{
    protected String title = "";
    protected GuiModelEditorPanel panel;

    public GuiModelEditorTab(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc);

        this.panel = panel;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return super.mouseClicked(context) || this.isEnabled() && this.area.isInside(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0x88000000);

        this.drawLabels();
        super.draw(context);
    }

    protected void drawLabels()
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 20, 0x88000000);

        this.font.drawStringWithShadow(this.title, this.area.x + 6, this.area.y + 6, 0xeeeeee);
    }
}