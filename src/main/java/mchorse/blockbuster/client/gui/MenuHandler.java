package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Main menu handler
 */
@SideOnly(Side.CLIENT)
public class MenuHandler
{
    /**
     * Refresh models, skins and morphs upon entering in Metamorph and/or
     * Blockbuster GUIs.
     */
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        GuiScreen gui = event.getGui();

        if (gui instanceof GuiMainMenu)
        {
            ModelExtrudedLayer.clear();
            ClientProxy.getDashboard(true).clear();
        }
    }
}