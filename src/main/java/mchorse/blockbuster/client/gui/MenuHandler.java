package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.metamorph.api.events.ReloadMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.DimensionManager;
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
        if (!Blockbuster.proxy.config.auto_refresh_models)
        {
            return;
        }

        GuiScreen gui = event.getGui();

        if (gui instanceof GuiMainMenu)
        {
            ModelExtrudedLayer.clear();
            ClientProxy.getDashboard(true).clear();
        }
    }

    /**
     * On morphs creative picker compilation, reload all morphs 
     */
    @SubscribeEvent
    public void onMorphsReload(ReloadMorphs event)
    {
        /* Reload models and skin */
        ModelPack pack = Blockbuster.proxy.models.pack;

        if (pack == null)
        {
            pack = Blockbuster.proxy.getPack();

            if (Minecraft.getMinecraft().isSingleplayer())
            {
                pack.addFolder(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/models");
            }
        }

        ClientProxy.actorPack.pack.reload();
        Blockbuster.proxy.loadModels(pack, false);
    }
}