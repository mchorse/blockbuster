package mchorse.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.client.gui.elements.GuiReplays;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorAdd;
import mchorse.blockbuster.network.common.director.PacketDirectorReset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Director Management Screen
 *
 * This GUI is responsible for managing director block's replays.
 */
@SideOnly(Side.CLIENT)
public class GuiDirectorNew extends GuiScreen
{
    /* Input */
    private BlockPos pos;

    /* GUI fields */
    private GuiButton done;
    private GuiButton reset;

    private GuiTextField replay;
    private GuiReplays replays;

    public GuiDirectorNew(BlockPos pos)
    {
        this.pos = pos;
        this.replays = new GuiReplays(this);
    }

    public void setCast(List<Replay> replays)
    {
        this.replays.setCast(replays);
    }

    /* Input handling */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            Minecraft.getMinecraft().displayGuiScreen(null);
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

        this.replays.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.replay.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        this.replay.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN && this.replay.isFocused())
        {
            this.addReplay();
        }
    }

    private void addReplay()
    {
        Dispatcher.sendToServer(new PacketDirectorAdd(this.pos, this.replay.getText()));

        this.replay.setText("");
    }

    /* Initiate GUI */

    @Override
    public void initGui()
    {
        int x = 6;
        int y = 6;
        int w = 120 - x * 2;
        int h = 20;

        /* Initiate fields */
        this.done = new GuiButton(0, this.width - 80 - x, this.height - y - h, 80, h, "Done");
        this.reset = new GuiButton(1, x, this.height - y - h, w, h, "Reset");

        this.replay = new GuiTextField(20, this.fontRendererObj, x + 1, y + 21, w - 2, h - 2);

        /* Adding GUI elements */
        this.buttonList.add(this.done);
        this.buttonList.add(this.reset);

        this.replays.updateRect(x, y + 40, w, (this.height - y * 2 - h - 40));
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.replays.setWorldAndResolution(mc, width, height);
    }

    /* Drawing */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawDefaultBackground();

        /* Vertical line that separates scroll list and actor editing */
        this.drawGradientRect(0, 0, 120, this.height, -1072689136, -804253680);

        /* Title */
        this.fontRendererObj.drawString("Director Block", 6, 6, 0xffffffff);

        /* Draw GUI fields */
        this.replay.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.replays.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void setSelected(Replay replay)
    {
        /* Sup! */
        System.out.println(replay);
    }
}