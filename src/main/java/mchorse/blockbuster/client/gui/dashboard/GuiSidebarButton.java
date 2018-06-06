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
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

            GuiInventory.drawItemStack(this.stack, this.x + this.width / 2 - 8, this.y + this.height / 2 - 8, null);

            GlStateManager.disableDepth();
            RenderHelper.disableStandardItemLighting();
        }
    }
}