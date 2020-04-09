package mchorse.blockbuster.client.gui.dashboard.panels;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * Main panel GUI element
 * 
 * This panel is basically used as information section and also as 
 * configuration panel for commonly used config options.
 */
public class GuiMainPanel extends GuiDashboardPanel
{
    public GuiMainPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        /* GuiElement element = this.first = new GuiButtonElement(mc, I18n.format("blockbuster.gui.main.wiki"), (button) -> GuiUtils.openWebLink(Blockbuster.WIKI_URL()));
        Resizer resizer = new Resizer().set(0, 40, 100, 20).parent(this.area).relative(previous.resizer());
        this.children.add(element.setResizer(resizer));

        element = new GuiButtonElement(mc, I18n.format("blockbuster.gui.main.discord"), (button) -> GuiUtils.openWebLink((Blockbuster.DISCORD_URL()));
        this.children.add(element.setResizer(new Resizer().set(0, 25, 100, 20).relative(resizer)));
        resizer = element.resizer();

        element = new GuiButtonElement(mc, I18n.format("blockbuster.gui.main.tutorial"), (button) -> GuiUtils.openWebLink((Blockbuster.TUTORIAL_URL()));
        this.children.add(element.setResizer(new Resizer().set(0, 25, 100, 20).relative(resizer)));
        resizer = element.resizer();

        element = new GuiButtonElement(mc, I18n.format("blockbuster.gui.main.models"), (button) -> GuiUtils.openWebLink(new File(ClientProxy.configFile, "models").toURI()));
        this.children.add(element.setResizer(new Resizer().set(0, 25, 100, 20).relative(resizer)));

        element = new GuiIconElement(mc, GuiDashboard.GUI_ICONS, 0, 0, 0, 16, (button) -> GuiUtils.openWebLink((Blockbuster.CHANNEL_URL()));
        resizer = new Resizer().set(0, 0, 16, 16).parent(this.area).x(1, -40).y(1, -20);
        this.children.add(element.setResizer(resizer));

        element = new GuiIconElement(mc, GuiDashboard.GUI_ICONS, 16, 0, 16, 16, (button) -> GuiUtils.openWebLink((Blockbuster.TWITTER_URL()));
        resizer = new Resizer().set(20, 0, 16, 16).relative(resizer);
        this.children.add(element.setResizer(resizer)); */
    }

    @Override
    public void resize()
    {
        /* if (GuiBase.getCurrent().screen.height > 260)
        {
            this.first.flex().set(0, 40, 100, 20).relative(this.last.resizer());
        }
        else
        {
            this.first.flex().set(0, 25, 100, 20).relative(this.area).x(1, -110);
        } */

        super.resize();
    }

    @Override
    public void draw(GuiContext context)
    {
        // this.font.drawStringWithShadow(I18n.format("blockbuster.gui.main.resources"), this.first.area.x, this.first.area.y - 15, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.main.options"), this.area.x + 10, this.area.y + 10, 0xffffff);
        this.font.drawStringWithShadow("McHorse", this.area.ex() - 90, this.area.ey() - 16, 0xffffff);

        super.draw(context);
    }
}
