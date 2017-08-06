package mchorse.blockbuster.aperture.gui;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.client.gui.config.AbstractGuiConfigOptions;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorPlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiDirectorConfigOptions extends AbstractGuiConfigOptions
{
    private String title = I18n.format("blockbuster.gui.aperture.config.title");

    public GuiCheckBox actions;
    public GuiCheckBox reload;
    public GuiButton reloadScene;

    public int max;
    public int x;
    public int y;

    public GuiDirectorConfigOptions(GuiCameraEditor editor)
    {
        super(editor);

        this.reload = new GuiCheckBox(-1, 0, 0, I18n.format("blockbuster.gui.aperture.config.reload"), CameraHandler.actions);
        this.reload.packedFGColour = 0xffffff;

        this.actions = new GuiCheckBox(-2, 0, 0, I18n.format("blockbuster.gui.aperture.config.actions"), CameraHandler.reload);
        this.actions.packedFGColour = 0xffffff;

        this.reloadScene = new GuiButton(-3, 0, 0, 100, 20, I18n.format("blockbuster.gui.aperture.config.reload_scene"));

        this.buttons.add(this.reload);
        this.buttons.add(this.actions);
        this.buttons.add(this.reloadScene);

        for (GuiButton button : this.buttons.buttons)
        {
            this.max = Math.max(this.max, button.width);
        }
    }

    @Override
    public int getWidth()
    {
        return Math.max(this.max + 8, Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.title) + 8);
    }

    @Override
    public int getHeight()
    {
        return this.buttons.buttons.size() * 20 + 16;
    }

    @Override
    public void update(int x, int y)
    {
        int i = 0;

        for (GuiButton button : this.buttons.buttons)
        {
            button.xPosition = x + 4;
            button.yPosition = y + 4 + i * 20 + 16;

            i++;
        }

        this.x = x;
        this.y = y;

        this.reload.setIsChecked(CameraHandler.reload);
        this.actions.setIsChecked(CameraHandler.actions);
    }

    @Override
    public boolean isActive()
    {
        return CameraHandler.getDirectorPos() != null;
    }

    @Override
    public void actionButtonPerformed(GuiButton button)
    {
        /* Options */
        int id = button.id;

        if (id == -1)
        {
            CameraHandler.reload = this.reload.isChecked();
        }
        else if (id == -2)
        {
            CameraHandler.actions = this.actions.isChecked();
        }
        else if (id == -3)
        {
            BlockPos pos = CameraHandler.getDirectorPos();

            Dispatcher.sendToServer(new PacketDirectorPlay(pos, PacketDirectorPlay.RESTART, ClientProxy.cameraEditor.scrub.value));
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        Minecraft.getMinecraft().fontRendererObj.drawString(this.title, this.x + 4, this.y + 4, 0xffffff, true);
    }
}