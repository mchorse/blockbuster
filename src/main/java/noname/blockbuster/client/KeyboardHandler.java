package noname.blockbuster.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketSwitchCamera;

/**
 * Separate event handler for keyboard events
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    private KeyBinding nextCamera;
    private KeyBinding previousCamera;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        this.nextCamera = new KeyBinding("key.blockbuster.next", Keyboard.KEY_RBRACKET, "key.blockbuster.camera");
        this.previousCamera = new KeyBinding("key.blockbuster.previous", Keyboard.KEY_LBRACKET, "key.blockbuster.camera");

        ClientRegistry.registerKeyBinding(this.nextCamera);
        ClientRegistry.registerKeyBinding(this.previousCamera);
    }

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        if (this.nextCamera.isPressed())
        {
            Dispatcher.getInstance().sendToServer(new PacketSwitchCamera(1));
        }

        if (this.previousCamera.isPressed())
        {
            Dispatcher.getInstance().sendToServer(new PacketSwitchCamera(-1));
        }
    }
}
