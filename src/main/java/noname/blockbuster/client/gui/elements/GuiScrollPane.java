package noname.blockbuster.client.gui.elements;

import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;

/**
 * Abstract class for scrolling elements.
 *
 * Supports custom drawing inside (see drawPane method), clipping, scroll bar
 * drawing, and background drawing.
 *
 * You can freely borrow this class, but keep the author annotation below and
 * this comment.
 *
 * @author mchorse
 */
public abstract class GuiScrollPane extends GuiScreen
{
    protected int x;
    protected int y;
    protected int w;
    protected int h;

    protected int scrollY = 0;
    protected int scrollHeight = 0;

    public GuiScrollPane(int x, int y, int w, int h)
    {
        this.updateRect(x, y, w, h);
    }

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

    /* Scroll content methods */

    public void scrollBy(int y)
    {
        this.scrollTo(this.scrollY + y);
    }

    public void scrollTo(int y)
    {
        this.scrollY = MathHelper.clamp_int(y, 0, this.scrollHeight - this.h);
    }

    /* Remapping buttons coordinates */

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = -Mouse.getEventDWheel();

        if (i != 0 && this.scrollHeight > this.h)
        {
            this.scrollBy((int) Math.copySign(1, i));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        mouseY += this.scrollY;

        if (mouseY < this.y || mouseY > this.y + this.h)
            return;

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        mouseY += this.scrollY;

        if (mouseY < this.y || mouseY > this.y + this.h)
            return;

        super.mouseReleased(mouseX, mouseY, state);
    }

    /* Drawing methods */

    protected abstract void drawPane();

    /**
     * Draw the background of the scroll pane
     */
    protected void drawBackground()
    {
        this.drawRect(this.x, this.y, this.x + this.w, this.y + this.h, -6250336);
        this.drawRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + this.h - 1, -16777216);
    }

    /**
     * Draw the scroll bar
     */
    protected void drawScrollBar()
    {
        if (this.scrollHeight < this.h)
            return;

        float progress = (float) this.scrollY / (float) (this.scrollHeight - this.h);
        int x = this.x + this.w - 8;
        float y = this.y + 3 + progress * (this.h - 26);

        this.drawRect(x, (int) y, x + 5, (int) y + 20, -6250336);
    }

    /**
     * Draw the pane
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
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
