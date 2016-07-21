package noname.blockbuster.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.camera.fixtures.IdleFixture;
import noname.blockbuster.commands.CommandCamera;

/**
 * Separate event handler for keyboard events
 *
 * This keyboard handler handles, yet, only key bindings related to camera.
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    /* Camera profile keys */
    private KeyBinding profileRemoveFixture;
    private KeyBinding profileAddIdleFixture;
    private KeyBinding profileToggleRender;
    private KeyBinding profileStartRunner;
    private KeyBinding profileStopRunner;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        String profile = "key.blockbuster.profile.title";

        /* Camera key bindings */
        this.profileRemoveFixture = new KeyBinding("key.blockbuster.profile.remove", Keyboard.KEY_M, profile);
        this.profileAddIdleFixture = new KeyBinding("key.blockbuster.profile.add", Keyboard.KEY_I, profile);
        this.profileToggleRender = new KeyBinding("key.blockbuster.profile.toggle", Keyboard.KEY_P, profile);
        this.profileStartRunner = new KeyBinding("key.blockbuster.profile.start", Keyboard.KEY_Z, profile);
        this.profileStopRunner = new KeyBinding("key.blockbuster.profile.stop", Keyboard.KEY_X, profile);

        ClientRegistry.registerKeyBinding(this.profileRemoveFixture);
        ClientRegistry.registerKeyBinding(this.profileAddIdleFixture);
        ClientRegistry.registerKeyBinding(this.profileToggleRender);
        ClientRegistry.registerKeyBinding(this.profileStartRunner);
        ClientRegistry.registerKeyBinding(this.profileStopRunner);
    }

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        this.handleCameraBindings();
    }

    /**
     * Here goes all key bindings that related to camera profile.
     */
    private void handleCameraBindings()
    {
        CameraProfile profile = CommandCamera.getProfile();

        if (this.profileRemoveFixture.isPressed() && profile.getCount() > 0)
        {
            profile.remove(profile.getCount() - 1);
        }

        if (this.profileAddIdleFixture.isPressed())
        {
            AbstractFixture fixture = new IdleFixture(1000);

            try
            {
                fixture.edit(new String[] {}, Minecraft.getMinecraft().thePlayer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            profile.add(fixture);
        }

        if (this.profileToggleRender.isPressed())
        {
            ClientProxy.profileRenderer.toggleRender();
        }

        if (this.profileStartRunner.isPressed())
        {
            ClientProxy.profileRunner.start();
        }
        else if (this.profileStopRunner.isPressed())
        {
            ClientProxy.profileRunner.stop();
        }
    }
}
