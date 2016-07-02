package noname.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.client.gui.elements.GuiCast;
import noname.blockbuster.client.gui.elements.GuiParentScreen;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorRequestCast;
import noname.blockbuster.network.common.director.PacketDirectorReset;

/**
 * Director block (the one for machinimas) GUI
 */
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

    public void setCast(List<String> actors, List<String> cameras)
    {
        this.cast.setCast(actors, cameras);
    }

    @Override
    public void appear(GuiScreen screen)
    {
        Dispatcher.getInstance().sendToServer(new PacketDirectorRequestCast(this.pos));
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
            Dispatcher.getInstance().sendToServer(new PacketDirectorReset(this.pos));
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

        this.done = new GuiButton(0, x, 205, 95, 20, I18n.format("blockbuster.gui.done"));
        this.reset = new GuiButton(1, x + 105, 205, 95, 20, I18n.format("blockbuster.gui.reset"));

        this.buttonList.add(this.done);
        this.buttonList.add(this.reset);

        this.cast.updateRect(this.width / 2 - 120, 40, 240, 155);
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
