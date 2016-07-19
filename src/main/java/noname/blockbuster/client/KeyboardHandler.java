package noname.blockbuster.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Separate event handler for keyboard events
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {}

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {}
}
