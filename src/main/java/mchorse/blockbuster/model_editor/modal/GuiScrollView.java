package mchorse.blockbuster.model_editor.modal;

import org.lwjgl.opengl.GL11;

import mchorse.metamorph.client.gui.utils.GuiScrollPane;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;

/**
 * Scroll view GUI
 *
 * This is a rip-off class from {@link GuiScrollPane} which allows using the
 * scrolling capabilities without having to be directly connected to the
 * current displayed GUI (basically avoiding stuff like
 * {@link GuiScreen#setWorldAndResolution(Minecraft, int, int)} and
 * {@link GuiScreen#handleInput()}).
 */
public abstract class GuiScrollView extends Gui
{
    protected Minecraft mc;
    protected GuiScreen parent;

    protected int x;
    protected int y;
    protected int w;
    protected int h;

    protected int scrollY = 0;
    protected int scrollHeight = 0;
    protected int scrollSpeed = 2;

    protected boolean dragging = false;
    protected boolean hidden = false;

    public boolean scrollOutside = false;

    public GuiScrollView(GuiScreen parent)
    {
        this.parent = parent;
        this.mc = parent.mc;
    }

    public void initiate()
    {}

    public void updateRect(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void setHeight(int height)
    {
        this.scrollHeight = height;
        this.scrollBy(0);
    }

    public int getHeight()
    {
        return this.scrollHeight;
    }

    public boolean isInside(int x, int y)
    {
        return !this.hidden && x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
    }

    /* Visibility */

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public boolean getHidden()
    {
        return this.hidden;
    }

    /* Scroll content methods */

    public void setScrollSpeed(int speed)
    {
        this.scrollSpeed = speed;
    }

    public void scrollBy(int y)
    {
        this.scrollTo(this.scrollY + y);
    }

    public void scrollTo(int y)
    {
        if (this.scrollHeight > this.h)
        {
            this.scrollY = MathHelper.clamp_int(y, 0, this.scrollHeight - this.h + 2);
        }
        else
        {
            this.scrollY = 0;
        }
    }

    /* Remapping buttons coordinates */

    public void scrollMouse(int scroll, int mouseX, int mouseY)
    {
        if (this.scrollOutside || this.isInside(mouseX, mouseY))
        {
            if (this.scrollHeight > this.h)
            {
                this.scrollBy((int) Math.copySign(this.scrollSpeed, scroll));
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseY < this.y || mouseY > this.y + this.h) return;

        int x = this.x + this.w - 8;
        int y = this.y + 3;

        if (mouseX >= x && mouseX <= x + 5 && mouseY >= y && mouseY <= y + this.h - 6)
        {
            this.dragging = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.dragging = false;

        if (mouseY < this.y || mouseY > this.y + this.h) return;
    }

    /* Drawing methods */

    protected abstract void drawView(int mouseX, int mouseY, float partialTicks);

    /**
     * Draw the background of the scroll pane
     */
    protected void drawBackground()
    {
        Gui.drawRect(this.x, this.y, this.x + this.w, this.y + this.h, -6250336);
        Gui.drawRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + this.h - 1, -16777216);
    }

    /**
     * Draw the scroll bar
     */
    protected void drawScrollBar()
    {
        if (this.scrollHeight < this.h) return;

        float progress = (float) this.scrollY / (float) (this.scrollHeight - this.h + 2);
        int x = this.x + this.w - 8;
        float y = this.y + 3 + progress * (this.h - 26);

        Gui.drawRect(x, (int) y, x + 5, (int) y + 20, -6250336);
    }

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.hidden)
        {
            return;
        }

        if (this.dragging)
        {
            int y = mouseY - this.y - 3;
            int h = this.h - 26;
            float progress = (float) y / (float) h;

            this.scrollTo((int) (progress * (this.scrollHeight - this.h + 2)));
        }

        this.drawBackground();

        GL11.glPushMatrix();
        GL11.glTranslatef(0, -this.scrollY, 0);

        GuiUtils.scissor(this.x, this.y, this.w, this.h, this.parent.width, this.parent.height);

        this.drawView(mouseX, mouseY, partialTicks);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();

        this.drawScrollBar();
    }
}