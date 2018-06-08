package mchorse.blockbuster.client.gui.dashboard.panels;

import mchorse.blockbuster.client.gui.framework.elements.GuiElements;
import mchorse.blockbuster.common.tileentity.director.Director;
import net.minecraft.client.Minecraft;

public class GuiDirectorPanel extends GuiDashboardPanel
{
    private Director director;

    private GuiElements subChildren;

    public GuiDirectorPanel(Minecraft mc)
    {
        super(mc);

        this.subChildren = new GuiElements();
    }

    public GuiDirectorPanel openDirector(Director director)
    {
        this.director = director;

        return this;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {

    }
}