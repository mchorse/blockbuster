package mchorse.blockbuster.config.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

/**
 * Config GUI factory
 *
 * It looks like the only important method here is {@link #mainConfigGuiClass()},
 * others are rare used and/or depreciated methods.
 */
public class GuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance)
    {}

    @Override
    public boolean hasConfigGui()
    {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new GuiConfig(parentScreen);
    }

    /* Section of some methods that never have been used, added just in case, I guess */

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }
}