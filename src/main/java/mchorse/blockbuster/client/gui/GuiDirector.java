package mchorse.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.client.gui.elements.GuiReplay;
import mchorse.blockbuster.client.gui.elements.GuiReplays;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorAdd;
import mchorse.blockbuster.network.common.director.PacketDirectorDetach;
import mchorse.blockbuster.network.common.director.PacketDirectorRemove;
import mchorse.blockbuster.network.common.director.PacketDirectorReset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Director Management Screen
 *
 * This GUI is responsible for managing director block's replays.
 */
@SideOnly(Side.CLIENT)
public class GuiDirector extends GuiScreen
{
    private String stringTitle = I18n.format("blockbuster.gui.director.title");

    /* Input */
    private BlockPos pos;

    /* GUI fields */
    private GuiButton done;
    private GuiButton reset;

    private GuiTextField replayName;
    private GuiReplays replays;
    private GuiReplay replay;

    private Replay previous;

    public GuiDirector(BlockPos pos)
    {
        this.pos = pos;
        this.replays = new GuiReplays(this);
        this.replay = new GuiReplay(this, pos);
    }

    /**
     * This method is invoked by one of the client handlers, I guess
     */
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
            if (this.previous != null)
            {
                this.replay.save(false);
            }

            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        else if (button.id == 1)
        {
            Dispatcher.sendToServer(new PacketDirectorReset(this.pos));

            this.previous = null;
            this.replay.select(null, -1);
            this.replays.reset();
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        this.replays.handleMouseInput();
        this.replay.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();

        this.replay.handleKeyboardInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.replayName.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        this.replayName.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN && this.replayName.isFocused())
        {
            this.addReplay();
        }
    }

    private void addReplay()
    {
        Dispatcher.sendToServer(new PacketDirectorAdd(this.pos, this.replayName.getText()));
        this.replayName.setText("");
    }

    /* Initiate GUI */

    @Override
    public void initGui()
    {
        int x = 8;
        int y = 8;
        int w = 120 - x * 2;
        int h = 20;

        /* Initiate fields */
        this.done = new GuiButton(0, this.width - 100 - x, this.height - y - h, 100, h, I18n.format("blockbuster.gui.done"));
        this.reset = new GuiButton(1, x, this.height - y - h, w, h, I18n.format("blockbuster.gui.reset"));

        this.replayName = new GuiTextField(20, this.fontRendererObj, x + 1, y + 16, w - 2, h - 2);

        /* Adding GUI elements */
        this.buttonList.add(this.done);
        this.buttonList.add(this.reset);

        this.replays.updateRect(x, y + 45, w, (this.height - y * 3 - h - 45));
        this.replay.initGui();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        this.replays.setWorldAndResolution(mc, width, height);
        this.replay.setWorldAndResolution(mc, width, height);
    }

    /* Drawing */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int y = 8;

        super.drawDefaultBackground();

        /* Vertical line that separates scroll list and actor editing */
        this.drawGradientRect(0, 0, 120, this.height, 0x44000000, 0x44000000);
        this.drawGradientRect(0, y + 45, 120, this.height - y * 2 - 20, 0xff000000, 0xff000000);

        /* Title */
        this.fontRendererObj.drawStringWithShadow(this.stringTitle, 8, 8, 0xffffffff);

        /* Draw GUI fields */
        this.replayName.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.replays.drawScreen(mouseX, mouseY, partialTicks);
        this.replay.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void setSelected(Replay replay, int index)
    {
        if (this.previous != null)
        {
            this.replay.save(true);
        }

        this.replay.select(replay, index);
        this.previous = replay;
    }

    public void remove(int index)
    {
        Dispatcher.sendToServer(new PacketDirectorRemove(this.pos, index));

        this.replay.select(null, -1);
        this.previous = null;
        this.replays.reset();
    }

    public void detach(int index)
    {
        Dispatcher.sendToServer(new PacketDirectorDetach(this.pos, index));
    }
}