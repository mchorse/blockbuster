package mchorse.blockbuster.client.gui.widgets;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
