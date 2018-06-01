package mchorse.blockbuster.client.gui.dashboard.panels;

import java.net.URI;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.Resizer;
import mchorse.blockbuster.client.gui.utils.Resizer.UnitMeasurement;
import net.minecraft.client.Minecraft;

/**
 * Main panel GUI element
 * 
 * This panel is basically used as information section and also as 
 * configuration panel for commonly used config options.
 */
public class GuiMainPanel extends GuiElement
{
    /**
     * Open web link in browser 
     */
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

        this.createChildren();

        GuiElement element = new GuiButtonElement(mc, "Wiki", (button) -> openWebLink("https://github.com/mchorse/blockbuster/wiki/"));
        Resizer resizer = null;
        this.children.add(element.setResizer(new Resizer().set(10, 25, 80, 20).setParent(this.area)));

        element = new GuiButtonElement(mc, "Discord", (button) -> openWebLink("https://discord.gg/qfxrqUF"));
        this.children.add(element.setResizer(new Resizer().set(10, 50, 80, 20).setParent(this.area)));

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 0, 0, 0, 16, (button) -> openWebLink("https://www.youtube.com/c/McHorse"));
        resizer = new Resizer().set(0, 0, 16, 16).setParent(this.area);
        resizer.x.set(1, UnitMeasurement.PERCENTAGE, -40);
        resizer.y.set(1, UnitMeasurement.PERCENTAGE, -20);
        this.children.add(element.setResizer(resizer));

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 16, 0, 16, 16, (button) -> openWebLink("https://twitter.com/McHorsy"));
        resizer = new Resizer().set(20, 0, 16, 16).setRelative(resizer);
        this.children.add(element.setResizer(resizer));
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.font.drawStringWithShadow("Resources", this.area.x + 10, this.area.y + 10, 0xffffff);

        super.draw(mouseX, mouseY, partialTicks);
    }
}