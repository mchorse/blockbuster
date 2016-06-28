package noname.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapRemove;

public class GuiCast extends GuiScreen
{
    public List<String> cast;
    public BlockPos pos;

    private int y = 0;
    private int h = 0;

    public GuiCast(BlockPos pos)
    {
        this.pos = pos;
    }

    public void setCast(List<String> cast)
    {
        this.cast = cast;
        this.h = cast.size() * 24;
        this.y = 0;
    }

    @Override
    public void initGui()
    {
        int w = 250;

        for (int i = 0; i < this.cast.size(); i++)
        {
            int x = this.width / 2 + w / 2 - 57;
            int y = 80 + i * 24;

            this.buttonList.add(new GuiButton(i, x, y + 3, 54, 18, I18n.format("blockbuster.gui.remove")));
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = -Mouse.getEventDWheel();

        if (i != 0 && this.h > 115)
        {
            i = (int) Math.copySign(1, i);
            this.y = MathHelper.clamp_int(this.y + i, 0, this.h - 115);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (this.cast.get(button.id) != null)
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorMapRemove(this.pos, this.cast.get(button.id)));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY + this.y, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY + this.y, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int w = 250;
        int x = this.width / 2 - w / 2;

        int rx = this.mc.displayWidth / this.width;
        int ry = this.mc.displayHeight / this.height;

        this.drawRect(x, 80, x + w, 195, -6250336);
        this.drawRect(x + 1, 81, x + w - 1, 194, -16777216);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, -this.y, 0);
        GL11.glScissor(x * rx, this.mc.displayHeight - 194 * ry, w * rx, 113 * ry);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        for (int i = 0, c = this.cast.size(); i < c; i++)
        {
            String member = this.cast.get(i);
            int y = 80 + i * 24;

            this.fontRendererObj.drawStringWithShadow(member, x + 6, y + 8, 0xffffff);
        }

        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY + this.y);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }
}
