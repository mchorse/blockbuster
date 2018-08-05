package mchorse.blockbuster.client.gui;

import java.io.IOException;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketConfirmBreak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

/**
 * Confirm breaking of director block GUI
 *
 * This GUI is responsible for displaying to a message box with two buttons for
 * confirming and canceling break of the block.
 */
public class GuiConfirm extends GuiScreen
{
    private String titleString = I18n.format("blockbuster.gui.confirm.title");

    private BlockPos pos;
    private String desc;

    private GuiButton cancel;
    private GuiButton breakBlock;

    public GuiConfirm(BlockPos pos, int count)
    {
        this.pos = pos;
        this.desc = I18n.format("blockbuster.gui.confirm.description", pos.getX(), pos.getY(), pos.getZ(), count);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
        {
            Dispatcher.sendToServer(new PacketConfirmBreak(this.pos, 0));
        }

        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    @Override
    public void initGui()
    {
        int x = this.width / 2;
        int y = this.height - 30;

        this.cancel = new GuiButton(0, x - 110, y, 100, 20, I18n.format("blockbuster.gui.cancel"));
        this.breakBlock = new GuiButton(1, x + 10, y, 100, 20, I18n.format("blockbuster.gui.break"));

        this.buttonList.add(this.cancel);
        this.buttonList.add(this.breakBlock);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRenderer, this.titleString, this.width / 2, 20, 0xffffffff);
        this.fontRenderer.drawSplitString(this.desc, this.width / 2 - this.width / 4, this.height / 2 - this.height / 4, this.width / 2, 0xffffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}