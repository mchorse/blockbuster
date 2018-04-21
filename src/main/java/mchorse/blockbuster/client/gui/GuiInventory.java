package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.utils.Area;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class GuiInventory
{
    public EntityPlayer player;
    public IInventoryPicker picker;
    public Area area = new Area();
    public boolean visible = false;

    public GuiInventory(IInventoryPicker picker, EntityPlayer player)
    {
        this.picker = picker;
        this.player = player;
    }

    public void update(int x, int y)
    {
        this.area.set(x - (int) (20 * 4.5), y - 40, 20 * 9, 20 * 4);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        int x = (mouseX - this.area.x - 2) / 20;
        int y = (mouseY - this.area.y - 2) / 20;

        if (x >= 9 || y >= 4 || x < 0 || y < 0 || !this.visible)
        {
            this.visible = false;

            return;
        }

        int index = x + y * 9;

        if (index >= 0 && index < 36 && this.picker != null)
        {
            this.picker.pickItem(this, this.player.inventory.mainInventory.get(index));
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (!this.visible)
        {
            return;
        }

        /* Background rendering */
        Gui.drawRect(this.area.x - 5, this.area.y - 4, this.area.x + this.area.w + 5, this.area.y + this.area.h + 4, 0xff000000);
        Gui.drawRect(this.area.x - 4, this.area.y - 5, this.area.x + this.area.w + 4, this.area.y + this.area.h + 5, 0xff000000);
        Gui.drawRect(this.area.x - 4, this.area.y - 4, this.area.x + this.area.w + 4, this.area.y + this.area.h + 4, 0xffffffff);
        Gui.drawRect(this.area.x - 2, this.area.y - 2, this.area.x + this.area.w + 2, this.area.y + this.area.h + 2, 0xffc6c6c6);

        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        NonNullList<ItemStack> inventory = this.player.inventory.mainInventory;

        for (int i = 0, c = inventory.size(); i < c; i++)
        {
            ItemStack stack = inventory.get(i);

            int x = i % 9;
            int y = i / 9;

            x = this.area.x + 2 + 20 * x;
            y = this.area.y + 2 + 20 * y;

            int diffX = mouseX - x;
            int diffY = mouseY - y;

            boolean hover = diffX >= 0 && diffX < 18 && diffY >= 0 && diffY < 18;

            Gui.drawRect(x - 1, y - 1, x + 17, y + 17, hover ? 0x88ffffff : 0x44000000);

            drawItemStack(stack, x, y, null);
        }

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Draws an ItemStack.
     *  
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    public static void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        itemRender.zLevel = 200.0F;

        FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = Minecraft.getMinecraft().fontRendererObj;

        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
        itemRender.zLevel = 0.0F;
        GlStateManager.popMatrix();
    }

    public static interface IInventoryPicker
    {
        public void pickItem(GuiInventory inventory, ItemStack stack);
    }
}