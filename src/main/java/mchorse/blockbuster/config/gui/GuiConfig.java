package mchorse.blockbuster.config.gui;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

/**
 * Config GUI
 *
 * This config GUI is responsible for managing Blockbuster's config. Most of
 * the code that implements config features is located in the parent of the
 * class.
 */
@SideOnly(Side.CLIENT)
public class GuiConfig extends cpw.mods.fml.client.config.GuiConfig
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
            elements.add(new ConfigElement(category));
        }

        return elements;
    }
}