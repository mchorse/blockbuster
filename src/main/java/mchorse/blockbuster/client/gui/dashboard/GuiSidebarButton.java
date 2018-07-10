package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.client.gui.widgets.GuiInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class GuiSidebarButton extends GuiButton
{
    public ItemStack stack;

    public GuiSidebarButton(int buttonId, int x, int y, ItemStack stack)
    {
        super(buttonId, x, y, "");
        this.stack = stack;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.xPosition + this.width / 2, this.yPosition + this.height / 2, 0);

            if (mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height)
            {
                GlStateManager.scale(1.5, 1.5, 1.5);
            }

            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

            GuiInventory.drawItemStack(this.stack, -8, -8, null);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            GlStateManager.popMatrix();
        }
    }
}