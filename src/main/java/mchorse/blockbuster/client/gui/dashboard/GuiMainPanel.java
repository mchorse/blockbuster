package mchorse.blockbuster.client.gui.dashboard;

import java.net.URI;

import mchorse.blockbuster.client.gui.framework.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.GuiElement;
import mchorse.blockbuster.client.gui.framework.GuiElements;
import mchorse.blockbuster.client.gui.utils.Resizer;
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
        this.elements.add(element.setResizer(new Resizer().set(10, 25, 80, 20).setParent(this.area)));

        element = new GuiButtonElement(mc, "Discord", (button) -> openWebLink("https://discord.gg/qfxrqUF"));
        this.elements.add(element.setResizer(new Resizer().set(10, 50, 80, 20).setParent(this.area)));

        element = new GuiButtonElement(mc, "YouTube", (button) -> openWebLink("https://www.youtube.com/c/McHorse"));
        this.elements.add(element.setResizer(new Resizer().set(10, 75, 80, 20).setParent(this.area)));

        element = new GuiButtonElement(mc, "Twitter", (button) -> openWebLink("https://twitter.com/McHorsy"));
        this.elements.add(element.setResizer(new Resizer().set(10, 100, 80, 20).setParent(this.area)));
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