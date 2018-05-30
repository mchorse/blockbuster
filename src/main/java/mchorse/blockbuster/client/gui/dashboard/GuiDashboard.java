package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.GuiInventory;
import mchorse.blockbuster.client.gui.framework.GuiBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiDashboard extends GuiBase
{
    public static final ResourceLocation ICONS = new ResourceLocation("blockbuster", "textures/gui/dashboard/icons.png");

    public ItemStack director;
    public ItemStack model_block;

    public GuiDashboard()
    {
        this.director = new ItemStack(Blockbuster.directorBlock);
        this.model_block = new ItemStack(Blockbuster.modelBlock);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.renderEngine.bindTexture(ICONS);
        GuiUtils.drawContinuousTexturedBox(0, 0, 0, 32, 32, this.height, 32, 32, 0, this.zLevel);
        mchorse.blockbuster.client.gui.utils.GuiUtils.drawHorizontalGradientRect(32, 0, 48, this.height, 0x22000000, 0x00000000, this.zLevel);
        mchorse.blockbuster.client.gui.utils.GuiUtils.drawHorizontalGradientRect(32 - 8, 0, 32, this.height, 0x00000000, 0x22000000, this.zLevel);

        this.drawTexturedModalRect(8, 8, 0, 0, 16, 16);

        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        GuiInventory.drawItemStack(this.director, 8, 32, null);
        GuiInventory.drawItemStack(this.model_block, 8, 32 + 24, null);

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}