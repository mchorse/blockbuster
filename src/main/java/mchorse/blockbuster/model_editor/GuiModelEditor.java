package mchorse.blockbuster.model_editor;

import net.minecraft.client.gui.GuiScreen;

/**
 * Model editor GUI
 */
public class GuiModelEditor extends GuiScreen
{
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.fontRendererObj.drawString("Hello!", 10, 10, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}