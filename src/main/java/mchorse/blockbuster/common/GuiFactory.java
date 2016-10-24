package mchorse.blockbuster.common;

import java.util.Set;

import mchorse.blockbuster.client.gui.GuiConfig;
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
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return GuiConfig.class;
    }

    /* Section of some methods that never have been used, added just in case, I guess */

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }
}