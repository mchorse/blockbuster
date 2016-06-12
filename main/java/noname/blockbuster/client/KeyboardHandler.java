package noname.blockbuster.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.SwitchCamera;

public class KeyboardHandler
{
    private KeyBinding nextCamera;
    private KeyBinding previousCamera;

    public KeyboardHandler()
    {
        this.nextCamera = new KeyBinding("Switch to the next camera", Keyboard.KEY_RBRACKET, "key.blockbuster.camera");
        this.previousCamera = new KeyBinding("Switch to the previous camera", Keyboard.KEY_LBRACKET, "key.blockbuster.camera");

        ClientRegistry.registerKeyBinding(this.nextCamera);
        ClientRegistry.registerKeyBinding(this.previousCamera);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        if (this.nextCamera.isPressed())
        {
            Dispatcher.getInstance().sendToServer(new SwitchCamera(1));
        }

        if (this.previousCamera.isPressed())
        {
            Dispatcher.getInstance().sendToServer(new SwitchCamera(-1));
        }
    }
}
