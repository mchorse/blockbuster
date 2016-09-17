package mchorse.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import mchorse.blockbuster.client.gui.elements.GuiCast;
import mchorse.blockbuster.client.gui.elements.GuiParentScreen;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorRequestCast;
import mchorse.blockbuster.network.common.director.PacketDirectorReset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Director block (the one for machinimas) GUI
 */
@SideOnly(Side.CLIENT)
public class GuiDirector extends GuiParentScreen
{
    /* Cached localized strings */
    private String title = I18n.format("blockbuster.director.title");

    /* Input data */
    private BlockPos pos;

    /* GUI fields */
    private GuiCast cast;
    private GuiButton done;
    private GuiButton reset;

    public GuiDirector(BlockPos pos)
    {
        this.pos = pos;
        this.cast = new GuiCast(this, pos);
    }

    public void setCast(List<Replay> actors)
    {
        this.cast.setCast(actors);
    }

    @Override
    public void appear(GuiScreen screen)
    {
        Dispatcher.sendToServer(new PacketDirectorRequestCast(this.pos));
    }

    /* Actions and handling */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(null);
        }
        else if (button.id == 1)
        {
            Dispatcher.sendToServer(new PacketDirectorReset(this.pos));
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.cast.handleMouseInput();
    }

    /* GUI and drawing */

    @Override
    public void initGui()
    {
        int w = 200;
        int x = this.width / 2 - w / 2;

        this.done = new GuiButton(0, x, this.height - 30, 95, 20, I18n.format("blockbuster.gui.done"));
        this.reset = new GuiButton(1, x + 105, this.height - 30, 95, 20, I18n.format("blockbuster.gui.reset"));

        this.buttonList.add(this.done);
        this.buttonList.add(this.reset);

        int y = 40;

        this.cast.updateRect(this.width / 2 - 120, y, 240, this.height - (y + 40));
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        this.cast.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 0xffffffff);

        this.cast.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
