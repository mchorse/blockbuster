package mchorse.blockbuster.client;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketTickMarker;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Separate event handler for keyboard events
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    /* Misc. */
    private KeyBinding dashboard;
    private KeyBinding modelEditor;
    private KeyBinding cameraMarker;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        /* Key categories */
        String category = "key.blockbuster.category";

        /* Misc */
        this.dashboard = new KeyBinding("key.blockbuster.dashboard", Keyboard.KEY_0, category);
        this.cameraMarker = new KeyBinding("key.blockbuster.marker", Keyboard.KEY_V, category);
        this.modelEditor = new KeyBinding("key.blockbuster.model_editor", Keyboard.KEY_NONE, category);

        ClientRegistry.registerKeyBinding(this.dashboard);
        ClientRegistry.registerKeyBinding(this.cameraMarker);
        ClientRegistry.registerKeyBinding(this.modelEditor);
    }

    @SubscribeEvent
    public void onUserLogOut(ClientDisconnectionFromServerEvent event)
    {
        GuiDashboard.reset();
        ClientProxy.manager.reset();
        ClientProxy.recordingOverlay.setVisible(false);
    }

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        if (this.dashboard.isPressed())
        {
            ClientProxy.getDashboard(false).open();
        }

        if (this.cameraMarker.isPressed())
        {
            Dispatcher.sendToServer(new PacketTickMarker());
        }

        if (this.modelEditor.isPressed())
        {
            GuiDashboard dashboard = ClientProxy.getDashboard(false);

            dashboard.open().openPanel(dashboard.modelEditorPanel);
        }
    }
}