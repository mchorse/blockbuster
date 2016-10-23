package mchorse.blockbuster.client.gui;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
            elements.add(new ConfigElement(Blockbuster.proxy.forge.getCategory(name).setLanguageKey("blockbuster.config.general")));
        }

        return elements;
    }
}