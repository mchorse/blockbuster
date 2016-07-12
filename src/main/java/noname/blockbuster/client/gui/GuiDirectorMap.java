package noname.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.gui.elements.GuiParentScreen;
import noname.blockbuster.client.gui.elements.GuiReplays;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapAdd;
import noname.blockbuster.network.common.director.PacketDirectorMapReset;

/**
 * Director map block GUI
 *
 * Helps manage adventure director block replays
 */
@SideOnly(Side.CLIENT)
public class GuiDirectorMap extends GuiParentScreen
{
    protected String title = I18n.format("blockbuster.director_map.title");

    protected GuiReplays cast;
    protected GuiButton done;
    protected GuiButton reset;
    protected GuiTextField input;
    protected GuiButton add;

    protected BlockPos pos;

    public GuiDirectorMap(BlockPos pos)
    {
        this.pos = pos;
        this.cast = new GuiReplays(this, pos);
    }

    public void setCast(List<String> cast)
    {
        this.cast.setCast(cast);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.cast.setWorldAndResolution(mc, width, height);
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
        this.cast.handleMouseInput();
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
        else if (button.id == 2 && !this.input.getText().isEmpty())
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorMapAdd(this.pos, this.input.getText()));
        }
    }

    /* GUI and drawing */

    @Override
    public void initGui()
    {
        int w = 200;
        int x = this.width / 2 - w / 2;

        this.input = new GuiTextField(3, this.fontRendererObj, x + 1, 51, 143, 18);
        this.input.setMaxStringLength(100);

        this.done = new GuiButton(0, x, this.height - 30, 95, 20, I18n.format("blockbuster.gui.done"));
        this.reset = new GuiButton(1, x + 105, this.height - 30, 95, 20, I18n.format("blockbuster.gui.reset"));
        this.add = new GuiButton(2, x + 155, 50, 45, 20, I18n.format("blockbuster.gui.add"));

        this.buttonList.add(this.done);
        this.buttonList.add(this.reset);
        this.buttonList.add(this.add);

        int y = 80;

        this.cast.updateRect(this.width / 2 - 120, y, 240, this.height - (y + 40));
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

        this.cast.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
