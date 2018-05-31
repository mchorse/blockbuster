package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.GuiInventory;
import mchorse.blockbuster.client.gui.framework.GuiElement;
import mchorse.blockbuster.client.gui.utils.Area;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiDashboardSidebar extends GuiElement
{
    public GuiDashboard dashboard;
    public ItemStack register;
    public ItemStack director;
    public ItemStack model;

    public Area mainArea = new Area();
    public Area directorArea = new Area();
    public Area modelArea = new Area();

    public GuiDashboardSidebar(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc);

        this.dashboard = dashboard;
        this.register = new ItemStack(Blockbuster.registerItem);
        this.director = new ItemStack(Blockbuster.directorBlock);
        this.model = new ItemStack(Blockbuster.modelBlock);
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.mainArea.set(this.area.x + 8, this.area.y + 8, 16, 16);
        this.directorArea.set(this.area.x + 8, this.area.y + 32, 16, 16);
        this.modelArea.set(this.area.x + 8, this.area.y + 32 + 24, 16, 16);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.mainArea.isInside(mouseX, mouseY))
        {
            this.dashboard.openPanel(this.dashboard.mainPanel);
        }

        if (this.directorArea.isInside(mouseX, mouseY))
        {
            this.dashboard.openPanel(this.dashboard.directorPanel);
        }

        if (this.modelArea.isInside(mouseX, mouseY))
        {
            this.dashboard.openPanel(this.dashboard.modelPanel);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        int h = this.area.h;
        int x = this.area.x + this.area.w;

        this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
        GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 32, this.area.w, h, 32, 32, 0, 0);
        mchorse.blockbuster.client.gui.utils.GuiUtils.drawHorizontalGradientRect(x, 0, x + 16, h, 0x22000000, 0x00000000, 0);
        mchorse.blockbuster.client.gui.utils.GuiUtils.drawHorizontalGradientRect(x - 8, 0, x, h, 0x00000000, 0x22000000, 0);

        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        GuiInventory.drawItemStack(this.register, this.area.x + 8, this.area.y + 8, null);
        GuiInventory.drawItemStack(this.director, this.area.x + 8, this.area.y + 32, null);
        GuiInventory.drawItemStack(this.model, this.area.x + 8, this.area.y + 32 + 24, null);

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }
}