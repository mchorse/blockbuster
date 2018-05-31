package mchorse.blockbuster.client.gui.dashboard;

import java.net.URI;

import mchorse.aperture.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.client.gui.framework.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.GuiElement;
import mchorse.blockbuster.client.gui.framework.GuiElements;
import mchorse.blockbuster.client.gui.utils.Resizer;
import mchorse.blockbuster.client.gui.utils.Resizer.UnitMeasurement;
import net.minecraft.client.Minecraft;

public class GuiMainPanel extends GuiElement
{
    public GuiElements elements = new GuiElements();

    public static void openWebLink(String address)
    {
        URI url = null;

        try
        {
            url = new URI(address);
        }
        catch (Exception e)
        {}

        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {url});
        }
        catch (Throwable t)
        {}
    }

    public GuiMainPanel(Minecraft mc)
    {
        super(mc);

        GuiElement element = new GuiButtonElement(mc, "Wiki", (button) -> openWebLink("https://github.com/mchorse/blockbuster/wiki/"));
        Resizer resizer = null;
        this.elements.add(element.setResizer(new Resizer().set(10, 25, 80, 20).setParent(this.area)));

        element = new GuiButtonElement(mc, "Discord", (button) -> openWebLink("https://discord.gg/qfxrqUF"));
        this.elements.add(element.setResizer(new Resizer().set(10, 50, 80, 20).setParent(this.area)));

        element = new GuiButtonElement(mc, new GuiTextureButton(0, 0, 0, GuiDashboard.ICONS).setTexPos(0, 0).setActiveTexPos(0, 16), (button) -> openWebLink("https://www.youtube.com/c/McHorse"));
        resizer = new Resizer().set(0, 0, 16, 16).setParent(this.area);
        resizer.x.set(1, UnitMeasurement.PERCENTAGE, -40);
        resizer.y.set(1, UnitMeasurement.PERCENTAGE, -20);
        this.elements.add(element.setResizer(resizer));

        element = new GuiButtonElement(mc, new GuiTextureButton(0, 0, 0, GuiDashboard.ICONS).setTexPos(16, 0).setActiveTexPos(16, 16), (button) -> openWebLink("https://twitter.com/McHorsy"));
        resizer = new Resizer().set(20, 0, 16, 16).setRelative(resizer);
        this.elements.add(element.setResizer(resizer));
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.elements.resize(width, height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.elements.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.elements.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.font.drawStringWithShadow("Resources", this.area.x + 10, this.area.y + 10, 0xffffff);

        this.elements.draw(mouseX, mouseY, partialTicks);
    }
}