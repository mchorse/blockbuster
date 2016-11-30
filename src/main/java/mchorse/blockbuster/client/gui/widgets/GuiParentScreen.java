package mchorse.blockbuster.client.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

/**
 * Parent screen
 *
 * Provides "appear" method for GuiChildScreen class
 *
 * @author mchorse
 */
@SideOnly(Side.CLIENT)
public abstract class GuiParentScreen extends GuiScreen
{
    public void appear(GuiScreen screen)
    {}
}
