package mchorse.blockbuster.config.gui;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Config GUI
 *
 * This config GUI is responsible for managing Blockbuster's config. Most of
 * the code that implements config features is located in the parent of the
 * class.
 */
@SideOnly(Side.CLIENT)
public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig
{
    public GuiConfig(GuiScreen parent)
    {
        super(parent, getConfigElements(), Blockbuster.MODID, false, false, "Blockbuster");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> elements = new ArrayList<IConfigElement>();

        for (String name : Blockbuster.proxy.forge.getCategoryNames())
        {
            ConfigCategory category = Blockbuster.proxy.forge.getCategory(name);
            category.setLanguageKey("blockbuster.config." + name + ".title");

            if (name.indexOf(".") == -1)
            {
                elements.add(new ConfigElement(category));
            }
        }

        return elements;
    }
}