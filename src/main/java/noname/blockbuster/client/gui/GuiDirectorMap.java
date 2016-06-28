package noname.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapAdd;
import noname.blockbuster.network.common.director.PacketDirectorMapReset;

/**
 * Director map block GUI
 *
 * Helps manage adventure director block replays
 */
public class GuiDirectorMap extends GuiScreen
{
    protected String title = I18n.format("blockbuster.director_map.title");
    protected String noCast = I18n.format("blockbuster.director.no_cast");

    protected GuiCast cast;
    protected GuiButton done;
    protected GuiButton reset;
    protected GuiTextField input;
    protected GuiButton add;

    protected int scale;
    protected BlockPos pos;

    public GuiDirectorMap(BlockPos pos)
    {
        this.pos = pos;
    }

    public void setCast(List<String> cast)
    {
        if (cast == null || cast.isEmpty())
        {
            this.cast = null;
            return;
        }

        this.cast = new GuiCast(this.pos);
        this.cast.setCast(cast);
        this.cast.setWorldAndResolution(this.mc, this.width, this.height);
    }

    @Override
    public void initGui()
    {
        int w = 200;
        int x = this.width / 2 - w / 2;

        this.input = new GuiTextField(3, this.fontRendererObj, x + 1, 51, 143, 18);
        this.input.setMaxStringLength(100);

        this.buttonList.add(this.done = new GuiButton(0, x, 205, 95, 20, I18n.format("blockbuster.gui.done")));
        this.buttonList.add(this.reset = new GuiButton(1, x + 105, 205, 95, 20, I18n.format("blockbuster.gui.reset")));
        this.buttonList.add(this.add = new GuiButton(2, x + 155, 50, 45, 20, I18n.format("blockbuster.gui.add")));

        this.scale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        this.input.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.input.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        if (this.cast != null)
        {
            this.cast.handleMouseInput();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(null);
        }
        else if (button.id == 1)
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorMapReset(this.pos));
        }
        else if (button.id == 2)
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorMapAdd(this.pos, this.input.getText()));
        }
    }

    /**
     * Draw all the elements on the screen
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 25, 0xffffffff);
        this.input.drawTextBox();

        if (this.cast != null)
        {
            this.cast.drawScreen(mouseX, mouseY, partialTicks);
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, this.noCast, this.width / 2, 80, 0xffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
