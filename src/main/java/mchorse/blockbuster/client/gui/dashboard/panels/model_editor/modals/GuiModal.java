package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class GuiModal extends GuiElement
{
    public GuiDelegateElement<IGuiElement> parent;

    public GuiModal(Minecraft mc, GuiDelegateElement<IGuiElement> parent)
    {
        super(mc);

        this.createChildren();
        this.parent = parent;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton) || this.area.isInside(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0xcc000000);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}