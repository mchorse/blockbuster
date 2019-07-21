package mchorse.blockbuster.client.gui.dashboard.panels;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Resizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiCheckBox;

/**
 * Main panel GUI element
 * 
 * This panel is basically used as information section and also as 
 * configuration panel for commonly used config options.
 */
public class GuiMainPanel extends GuiDashboardPanel
{
    public List<GuiConfigOption> options = new ArrayList<GuiConfigOption>();

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

        GuiElement element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.main.wiki"), (button) -> openWebLink("https://github.com/mchorse/blockbuster/wiki/"));
        Resizer resizer = new Resizer().set(10, 25, 80, 20).parent(this.area).x(1, -90);
        this.children.add(element.setResizer(resizer));

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.main.discord"), (button) -> openWebLink("https://discord.gg/qfxrqUF"));
        this.children.add(element.setResizer(new Resizer().set(0, 25, 80, 20).relative(resizer)));

        element = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 0, 0, 0, 16, (button) -> openWebLink("https://www.youtube.com/c/McHorse"));
        resizer = new Resizer().set(0, 0, 16, 16).parent(this.area).x(1, -40).y(1, -20);
        this.children.add(element.setResizer(resizer));

        element = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 16, 0, 16, 16, (button) -> openWebLink("https://twitter.com/McHorsy"));
        resizer = new Resizer().set(20, 0, 16, 16).relative(resizer);
        this.children.add(element.setResizer(resizer));

        this.options.add(new GuiConfigOption("green_screen_sky", Configuration.CATEGORY_GENERAL));
        this.options.add(new GuiConfigOption("record_commands", "recording"));
        this.options.add(new GuiConfigOption("actor_always_render", "actor"));
        this.options.add(new GuiConfigOption("actor_always_render_names", "actor"));
        this.options.add(new GuiConfigOption("damage_control", "damage_control"));
        this.options.add(new GuiConfigOption("model_block_disable_rendering", "model_block"));
        this.options.add(new GuiConfigOption("model_block_disable_item_rendering", "model_block"));

        GuiButtonElement<GuiCheckBox> previous = null;

        for (GuiConfigOption option : this.options)
        {
            boolean value = this.getProp(option).getBoolean();
            String label = I18n.format("blockbuster.config." + option.category + "." + option.name);

            option.button = GuiButtonElement.checkbox(mc, label, value, (button) -> this.setOption(option));

            if (previous == null)
            {
                option.button.resizer().set(10, 25, option.button.button.width, 11).parent(this.area);
            }
            else
            {
                option.button.resizer().set(0, 16, option.button.button.width, 11).relative(previous.resizer());
            }

            previous = option.button;
            this.children.add(option.button);
        }
    }

    private void setOption(GuiConfigOption option)
    {
        this.getProp(option).set(option.button.button.isChecked());
    }

    private Property getProp(GuiConfigOption option)
    {
        return Blockbuster.proxy.forge.getCategory(option.category).get(option.name);
    }

    @Override
    public void appear()
    {
        for (GuiConfigOption option : this.options)
        {
            option.button.button.setIsChecked(this.getProp(option).getBoolean());
        }
    }

    @Override
    public void close()
    {
        Blockbuster.proxy.config.reload();
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.main.resources"), this.area.getX(1) - 90, this.area.y + 10, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.main.options"), this.area.x + 10, this.area.y + 10, 0xffffff);
        this.font.drawStringWithShadow("McHorse", this.area.getX(1) - 90, this.area.getY(1) - 16, 0xffffff);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    public static class GuiConfigOption
    {
        public String name;
        public String category;
        public GuiButtonElement<GuiCheckBox> button;

        public GuiConfigOption(String name, String category)
        {
            this.name = name;
            this.category = category;
        }
    }
}