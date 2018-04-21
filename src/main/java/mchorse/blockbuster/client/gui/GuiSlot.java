package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.utils.Area;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiSlot
{
    public static final ResourceLocation SHIELD = new ResourceLocation("minecraft:textures/items/empty_armor_slot_shield.png");
    public static final ResourceLocation BOOTS = new ResourceLocation("minecraft:textures/items/empty_armor_slot_boots.png");
    public static final ResourceLocation LEGGINGS = new ResourceLocation("minecraft:textures/items/empty_armor_slot_leggings.png");
    public static final ResourceLocation CHESTPLATE = new ResourceLocation("minecraft:textures/items/empty_armor_slot_chestplate.png");
    public static final ResourceLocation HELMET = new ResourceLocation("minecraft:textures/items/empty_armor_slot_helmet.png");

    public int slot;
    public ItemStack stack;
    public Area area = new Area();

    public GuiSlot(int slot)
    {
        this.slot = slot;
    }

    public void update(int x, int y)
    {
        this.area.set(x, y, 20, 20);
    }

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.area.x - 3, this.area.y - 2, this.area.x + this.area.w + 3, this.area.y + this.area.h + 2, 0xff000000);
        Gui.drawRect(this.area.x - 2, this.area.y - 3, this.area.x + this.area.w + 2, this.area.y + this.area.h + 3, 0xff000000);
        Gui.drawRect(this.area.x - 2, this.area.y - 2, this.area.x + this.area.w + 2, this.area.y + this.area.h + 2, 0xffffffff);
        Gui.drawRect(this.area.x, this.area.y, this.area.x + this.area.w, this.area.y + this.area.h, 0xffc6c6c6);

        int x = this.area.x + 2;
        int y = this.area.y + 2;

        if (this.stack.isEmpty() && this.slot != 0)
        {
            GlStateManager.enableAlpha();

            if (this.slot == 1)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(SHIELD);
            }
            else if (this.slot == 2)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(BOOTS);
            }
            else if (this.slot == 3)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(LEGGINGS);
            }
            else if (this.slot == 4)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(CHESTPLATE);
            }
            else if (this.slot == 5)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(HELMET);
            }

            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
        }
        else
        {
            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.enableDepth();

            GuiInventory.drawItemStack(this.stack, x, y, null);

            GlStateManager.disableDepth();
            RenderHelper.disableStandardItemLighting();
        }
    }
}