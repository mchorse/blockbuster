package mchorse.blockbuster.client;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.GuiGun;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Separate event handler for keyboard events
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    /* Misc. */
    private KeyBinding plauseDirector;
    private KeyBinding recordDirector;
    private KeyBinding pauseDirector;
    private KeyBinding openGun;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        /* Key categories */
        String category = "key.blockbuster.category";

        /* Misc */
        this.plauseDirector = new KeyBinding("key.blockbuster.plause_director", Keyboard.KEY_RCONTROL, category);
        this.recordDirector = new KeyBinding("key.blockbuster.record_director", Keyboard.KEY_RMENU, category);
        this.pauseDirector = new KeyBinding("key.blockbuster.pause_director", Keyboard.KEY_RSHIFT, category);
        this.openGun = new KeyBinding("key.blockbuster.open_gun", Keyboard.KEY_NONE, category);

        ClientRegistry.registerKeyBinding(this.plauseDirector);
        ClientRegistry.registerKeyBinding(this.recordDirector);
        ClientRegistry.registerKeyBinding(this.pauseDirector);
        ClientRegistry.registerKeyBinding(this.openGun);
    }

    @SubscribeEvent
    public void onUserLogOut(ClientDisconnectionFromServerEvent event)
    {
        GuiDashboard.reset();
        ClientProxy.manager.reset();
        ClientProxy.recordingOverlay.setVisible(false);

        Minecraft.getMinecraft().addScheduledTask(() -> StructureMorph.cleanUp());
    }

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        if (this.plauseDirector.isPressed())
        {
            GuiDashboard dash = ClientProxy.dashboard;

            if (dash != null && dash.directorPanel != null)
            {
                dash.directorPanel.plause();
            }
        }

        if (this.recordDirector.isPressed())
        {
            GuiDashboard dash = ClientProxy.dashboard;

            if (dash != null && dash.directorPanel != null)
            {
                dash.directorPanel.record();
            }
        }

        if (this.pauseDirector.isPressed())
        {
            GuiDashboard dash = ClientProxy.dashboard;

            if (dash != null && dash.directorPanel != null)
            {
                dash.directorPanel.pause();
            }
        }

        if (this.openGun.isPressed())
        {
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack stack = mc.player.getHeldItemMainhand();

            if (stack.getItem() == Blockbuster.gunItem)
            {
                mc.displayGuiScreen(new GuiGun(stack));
            }
        }
    }
}