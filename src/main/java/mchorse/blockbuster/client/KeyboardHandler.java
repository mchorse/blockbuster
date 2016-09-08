package mchorse.blockbuster.client;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.camera.CameraControl;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.camera.fixtures.CircularFixture;
import mchorse.blockbuster.camera.fixtures.FollowFixture;
import mchorse.blockbuster.camera.fixtures.IdleFixture;
import mchorse.blockbuster.camera.fixtures.LookFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketCameraMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Separate event handler for keyboard events
 *
 * This keyboard handler handles, yet, only key bindings related to the camera,
 * and by the way, there are a log of them!
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    /* Camera profile keys */
    private KeyBinding profileAddIdleFixture;
    private KeyBinding profileAddPathFixture;
    private KeyBinding profileAddLookFixture;
    private KeyBinding profileAddFollowFixture;
    private KeyBinding profileAddCircularFixture;

    private KeyBinding profileEditFixture;
    private KeyBinding profileRemoveFixture;

    private KeyBinding profileAddDuration;
    private KeyBinding profileReduceDuration;

    private KeyBinding profileAddPathPoint;
    private KeyBinding profileRemovePathPoint;

    private KeyBinding profileGoToFixture;
    private KeyBinding profileToggleRender;

    private KeyBinding profileLoadProfile;
    private KeyBinding profileSaveProfile;

    private KeyBinding profileNextFixture;
    private KeyBinding profilePrevFixture;

    private KeyBinding profileStartRunner;
    private KeyBinding profileStopRunner;

    /* Undocumented feature!!! */
    private KeyBinding featureUndocumented;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        String general = "key.blockbuster.general";
        String profile = "key.blockbuster.profile";
        String duration = "key.blockbuster.duration";
        String path = "key.blockbuster.path";

        /* Camera key bindings */
        this.profileAddIdleFixture = new KeyBinding("key.blockbuster.profile.add_idle", Keyboard.KEY_NONE, profile);
        this.profileAddPathFixture = new KeyBinding("key.blockbuster.profile.add_path", Keyboard.KEY_NONE, profile);
        this.profileAddLookFixture = new KeyBinding("key.blockbuster.profile.add_look", Keyboard.KEY_NONE, profile);
        this.profileAddFollowFixture = new KeyBinding("key.blockbuster.profile.add_follow", Keyboard.KEY_NONE, profile);
        this.profileAddCircularFixture = new KeyBinding("key.blockbuster.profile.add_circular", Keyboard.KEY_NONE, profile);

        this.profileEditFixture = new KeyBinding("key.blockbuster.profile.edit", Keyboard.KEY_NONE, profile);
        this.profileRemoveFixture = new KeyBinding("key.blockbuster.profile.remove", Keyboard.KEY_NONE, profile);

        this.profileAddDuration = new KeyBinding("key.blockbuster.profile.add_duration", Keyboard.KEY_NONE, duration);
        this.profileReduceDuration = new KeyBinding("key.blockbuster.profile.reduce_duration", Keyboard.KEY_NONE, duration);

        this.profileAddPathPoint = new KeyBinding("key.blockbuster.profile.add_path_point", Keyboard.KEY_NONE, path);
        this.profileRemovePathPoint = new KeyBinding("key.blockbuster.profile.remove_path_point", Keyboard.KEY_NONE, path);

        this.profileSaveProfile = new KeyBinding("key.blockbuster.profile.save", Keyboard.KEY_NONE, profile);
        this.profileLoadProfile = new KeyBinding("key.blockbuster.profile.load", Keyboard.KEY_NONE, profile);

        this.profileGoToFixture = new KeyBinding("key.blockbuster.profile.goto", Keyboard.KEY_G, general);
        this.profileToggleRender = new KeyBinding("key.blockbuster.profile.toggle", Keyboard.KEY_P, general);
        this.profileNextFixture = new KeyBinding("key.blockbuster.profile.next", Keyboard.KEY_RBRACKET, general);
        this.profilePrevFixture = new KeyBinding("key.blockbuster.profile.prev", Keyboard.KEY_LBRACKET, general);
        this.profileStartRunner = new KeyBinding("key.blockbuster.profile.start", Keyboard.KEY_Z, general);
        this.profileStopRunner = new KeyBinding("key.blockbuster.profile.stop", Keyboard.KEY_X, general);

        /* Undocumented */
        this.featureUndocumented = new KeyBinding("key.blockbuster.feature", Keyboard.KEY_V, general);

        /* Add all key bindings to client registry */
        ClientRegistry.registerKeyBinding(this.profileAddIdleFixture);
        ClientRegistry.registerKeyBinding(this.profileAddPathFixture);
        ClientRegistry.registerKeyBinding(this.profileAddLookFixture);
        ClientRegistry.registerKeyBinding(this.profileAddFollowFixture);
        ClientRegistry.registerKeyBinding(this.profileAddCircularFixture);

        ClientRegistry.registerKeyBinding(this.profileEditFixture);
        ClientRegistry.registerKeyBinding(this.profileRemoveFixture);

        ClientRegistry.registerKeyBinding(this.profileAddDuration);
        ClientRegistry.registerKeyBinding(this.profileReduceDuration);

        ClientRegistry.registerKeyBinding(this.profileAddPathPoint);
        ClientRegistry.registerKeyBinding(this.profileRemovePathPoint);

        ClientRegistry.registerKeyBinding(this.profileGoToFixture);
        ClientRegistry.registerKeyBinding(this.profileToggleRender);

        ClientRegistry.registerKeyBinding(this.profileSaveProfile);
        ClientRegistry.registerKeyBinding(this.profileLoadProfile);

        ClientRegistry.registerKeyBinding(this.profileNextFixture);
        ClientRegistry.registerKeyBinding(this.profilePrevFixture);

        ClientRegistry.registerKeyBinding(this.profileStartRunner);
        ClientRegistry.registerKeyBinding(this.profileStopRunner);

        /* Wow, so undocumented!!! */
        ClientRegistry.registerKeyBinding(this.featureUndocumented);
    }

    @SubscribeEvent
    public void onUserLogIn(ClientConnectedToServerEvent event)
    {
        CommandCamera.reset();
    }

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        try
        {
            this.handleCameraBindings(player);
        }
        catch (CommandException e)
        {
            ITextComponent message = new TextComponentString(I18n.format(e.getMessage(), e.getErrorObjects()));

            player.addChatMessage(message);
        }
    }

    /**
     * Here goes all key bindings that related to camera profile.
     */
    private void handleCameraBindings(EntityPlayer player) throws CommandException
    {
        CameraControl control = CommandCamera.getControl();

        /* Adding fixture */
        if (this.profileAddIdleFixture.isPressed())
        {
            control.add(player, new IdleFixture(1000));
        }
        else if (this.profileAddPathFixture.isPressed())
        {
            control.add(player, new PathFixture(1000));
        }
        else if (this.profileAddLookFixture.isPressed())
        {
            control.add(player, new LookFixture(1000));
        }
        else if (this.profileAddFollowFixture.isPressed())
        {
            control.add(player, new FollowFixture(1000));
        }
        else if (this.profileAddCircularFixture.isPressed())
        {
            control.add(player, new CircularFixture(1000));
        }

        /* More management */
        if (this.profileEditFixture.isPressed())
        {
            control.edit(player);
        }

        if (this.profileRemoveFixture.isPressed())
        {
            control.remove();
        }

        /* Duration management */
        if (this.profileAddDuration.isPressed())
        {
            control.addDuration(100);
        }
        else if (this.profileReduceDuration.isPressed())
        {
            control.addDuration(-100);
        }

        /* Path fixture management */
        if (this.profileAddPathPoint.isPressed())
        {
            control.addPoint(new Position(player));
        }
        else if (this.profileRemovePathPoint.isPressed())
        {
            control.removePoint();
        }

        /* Utilities */
        if (this.profileGoToFixture.isPressed())
        {
            control.goTo(player);
        }

        if (this.profileToggleRender.isPressed())
        {
            ClientProxy.profileRenderer.toggleRender();
        }

        /* Save and reload */
        if (this.profileSaveProfile.isPressed())
        {
            control.save();
        }
        else if (this.profileLoadProfile.isPressed())
        {
            control.load();
        }

        /* Navigation */
        if (this.profileNextFixture.isPressed())
        {
            control.next();
        }
        else if (this.profilePrevFixture.isPressed())
        {
            control.prev();
        }

        /* Starting stopping */
        if (this.profileStartRunner.isPressed())
        {
            ClientProxy.profileRunner.start();
        }
        else if (this.profileStopRunner.isPressed())
        {
            ClientProxy.profileRunner.stop();
        }

        if (this.featureUndocumented.isPressed())
        {
            Dispatcher.sendToServer(new PacketCameraMarker());
        }
    }
}