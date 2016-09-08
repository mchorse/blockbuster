package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Abstract class for scrolling elements.
 *
 * Supports custom drawing inside (see drawPane method), clipping, scroll bar
 * drawing, and background drawing.
 *
 * You can freely borrow this class, but keep the author annotation below and
 * this comment.
 *
 * By the way, this class doesn't support nested scroll panes.
 *
 * @author mchorse
 */
@SideOnly(Side.CLIENT)
public abstract class GuiScrollPane extends GuiScreen
{
    protected int x;
    protected int y;
    protected int w;
    protected int h;

    protected int scrollY = 0;
    protected int scrollHeight = 0;

    private boolean dragging = false;
    protected boolean hidden = false;

    public void updateRect(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void setHeight(int height)
    {
        this.scrollY = 0;
        this.scrollHeight = height;
    }

    public boolean isInside(int x, int y)
    {
        return !this.hidden && x >= this.x && this.x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
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

    public void scrollBy(int y)
    {
        this.scrollTo(this.scrollY + y);
    }

    public void scrollTo(int y)
    {
        this.scrollY = MathHelper.clamp_int(y, 0, this.scrollHeight - this.h + 2);
    }

    /* Remapping buttons coordinates */

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = -Mouse.getEventDWheel();

        if (i != 0 && this.scrollHeight > this.h)
        {
            this.scrollBy((int) Math.copySign(2, i));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseY < this.y || mouseY > this.y + this.h) return;

        int x = this.x + this.w - 8;
        int y = this.y + 3;

        if (mouseX >= x && mouseX <= x + 5 && mouseY >= y && mouseY <= y + this.h - 6)
        {
            this.dragging = true;
        }

        super.mouseClicked(mouseX, mouseY + this.scrollY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.dragging = false;

        if (mouseY < this.y || mouseY > this.y + this.h) return;

        super.mouseReleased(mouseX, mouseY + this.scrollY, state);
    }

    /* Drawing methods */

    protected abstract void drawPane();

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

    /**
     * Draw the pane
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
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

        /* F*$! those ints */
        float rx = (float) this.mc.displayWidth / (float) this.width;
        float ry = (float) this.mc.displayHeight / (float) this.height;

        this.drawBackground();

        GL11.glPushMatrix();
        GL11.glTranslatef(0, -this.scrollY, 0);

        /* Clipping area around scroll area */
        int x = (int) ((this.x + 1) * rx);
        int y = (int) (this.mc.displayHeight - (this.y + this.h - 1) * ry);
        int w = (int) ((this.w - 2) * rx);
        int h = (int) ((this.h - 2) * ry);

        GL11.glScissor(x, y, w, h);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        this.drawPane();
        super.drawScreen(mouseX, mouseY + this.scrollY, partialTicks);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();

        this.drawScrollBar();
    }
}
