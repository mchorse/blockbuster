package mchorse.blockbuster.client.gui.widgets.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiTextureButton extends GuiButton
{
    public ResourceLocation texture;

    public int tx;
    public int ty;

    public int atx;
    public int aty;

    public GuiTextureButton(int id, int x, int y, ResourceLocation texture)
    {
        super(id, x, y, 16, 16, "");
        this.texture = texture;
    }

    public GuiTextureButton setTexPos(int x, int y)
    {
        this.tx = x;
        this.ty = y;

        return this;
    }

    public GuiTextureButton setActiveTexPos(int x, int y)
    {
        this.atx = x;
        this.aty = y;

        return this;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.renderEngine.bindTexture(this.texture);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.drawTexturedModalRect(this.xPosition, this.yPosition, this.hovered ? this.atx : this.tx, this.hovered ? this.aty : this.ty, this.width, this.height);
        }
    }
}