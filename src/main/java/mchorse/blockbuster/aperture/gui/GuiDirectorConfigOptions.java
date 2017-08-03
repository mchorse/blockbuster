package mchorse.blockbuster.aperture.gui;

import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.client.gui.config.AbstractGuiConfigOptions;
import mchorse.blockbuster.aperture.CameraHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiDirectorConfigOptions extends AbstractGuiConfigOptions
{
    public GuiCheckBox actions;
    public GuiCheckBox reload;

    public int max;
    public int x;
    public int y;

    public GuiDirectorConfigOptions(GuiCameraEditor editor)
    {
        super(editor);

        this.reload = new GuiCheckBox(-1, 0, 0, "Reload actors", CameraHandler.actions);
        this.reload.packedFGColour = 0xffffff;

        this.actions = new GuiCheckBox(-2, 0, 0, "Preview actions", CameraHandler.reload);
        this.actions.packedFGColour = 0xffffff;

        this.buttons.add(this.reload);
        this.buttons.add(this.actions);

        for (GuiButton button : this.buttons.buttons)
        {
            this.max = Math.max(this.max, button.width);
        }
    }

    @Override
    public int getWidth()
    {
        return Math.max(this.max + 8, Minecraft.getMinecraft().fontRendererObj.getStringWidth("Director") + 8);
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
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        Minecraft.getMinecraft().fontRendererObj.drawString("Director", this.x + 4, this.y + 4, 0xffffff, true);
    }
}