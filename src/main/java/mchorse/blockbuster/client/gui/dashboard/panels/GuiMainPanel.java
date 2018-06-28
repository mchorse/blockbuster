package mchorse.blockbuster.client.gui.dashboard.panels;

import java.net.URI;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.Resizer;
import net.minecraft.client.Minecraft;

/**
 * Main panel GUI element
 * 
 * This panel is basically used as information section and also as 
 * configuration panel for commonly used config options.
 */
public class GuiMainPanel extends GuiDashboardPanel
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

    public GuiMainPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        GuiElement element = GuiButtonElement.button(mc, "Wiki", (button) -> openWebLink("https://github.com/mchorse/blockbuster/wiki/"));
        Resizer resizer = new Resizer().set(10, 25, 80, 20).parent(this.area).x(1, -90);
        this.children.add(element.setResizer(resizer));

        element = GuiButtonElement.button(mc, "Discord", (button) -> openWebLink("https://discord.gg/qfxrqUF"));
        this.children.add(element.setResizer(new Resizer().set(0, 25, 80, 20).relative(resizer)));

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 0, 0, 0, 16, (button) -> openWebLink("https://www.youtube.com/c/McHorse"));
        resizer = new Resizer().set(0, 0, 16, 16).parent(this.area).x(1, -40).y(1, -20);
        this.children.add(element.setResizer(resizer));

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 16, 0, 16, 16, (button) -> openWebLink("https://twitter.com/McHorsy"));
        resizer = new Resizer().set(20, 0, 16, 16).relative(resizer);
        this.children.add(element.setResizer(resizer));
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.font.drawStringWithShadow("Resources", this.area.getX(1) - 90, this.area.y + 10, 0xffffff);
        this.font.drawStringWithShadow("McHorse", this.area.getX(1) - 90, this.area.getY(1) - 16, 0xffffff);

        super.draw(mouseX, mouseY, partialTicks);
    }
}