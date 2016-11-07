package mchorse.blockbuster.client;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.CameraControl;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.camera.fixtures.CircularFixture;
import mchorse.blockbuster.camera.fixtures.FollowFixture;
import mchorse.blockbuster.camera.fixtures.IdleFixture;
import mchorse.blockbuster.camera.fixtures.LookFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketCameraMarker;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
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
    private KeyBinding addIdleFixture;
    private KeyBinding addPathFixture;
    private KeyBinding addLookFixture;
    private KeyBinding addFollowFixture;
    private KeyBinding addCircularFixture;

    private KeyBinding editFixture;
    private KeyBinding removeFixture;

    private KeyBinding increaseDuration;
    private KeyBinding reduceDuration;

    private KeyBinding addPathPoint;
    private KeyBinding removePathPoint;

    private KeyBinding loadProfile;
    private KeyBinding saveProfile;

    private KeyBinding gotoFixture;
    private KeyBinding toggleRender;

    private KeyBinding nextFixture;
    private KeyBinding prevFixture;

    private KeyBinding startRunning;
    private KeyBinding stopRunning;

    private KeyBinding cameraMarker;

    private KeyBinding addRoll;
    private KeyBinding reduceRoll;
    private KeyBinding resetRoll;

    private KeyBinding addFov;
    private KeyBinding reduceFov;
    private KeyBinding resetFov;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        /* Key categories */
        String general = "key.blockbuster.general";
        String fixtures = "key.blockbuster.fixtures.title";
        String camera = "key.blockbuster.camera";
        String duration = "key.blockbuster.duration.title";
        String path = "key.blockbuster.path.title";
        String misc = "key.blockbuster.misc";

        /* Camera fixtures key bindings */
        this.addIdleFixture = new KeyBinding("key.blockbuster.fixtures.idle", Keyboard.KEY_NONE, fixtures);
        this.addPathFixture = new KeyBinding("key.blockbuster.fixtures.path", Keyboard.KEY_NONE, fixtures);
        this.addLookFixture = new KeyBinding("key.blockbuster.fixtures.look", Keyboard.KEY_NONE, fixtures);
        this.addFollowFixture = new KeyBinding("key.blockbuster.fixtures.follow", Keyboard.KEY_NONE, fixtures);
        this.addCircularFixture = new KeyBinding("key.blockbuster.fixtures.circular", Keyboard.KEY_NONE, fixtures);

        ClientRegistry.registerKeyBinding(this.addIdleFixture);
        ClientRegistry.registerKeyBinding(this.addPathFixture);
        ClientRegistry.registerKeyBinding(this.addLookFixture);
        ClientRegistry.registerKeyBinding(this.addFollowFixture);
        ClientRegistry.registerKeyBinding(this.addCircularFixture);

        this.editFixture = new KeyBinding("key.blockbuster.fixtures.edit", Keyboard.KEY_NONE, fixtures);
        this.removeFixture = new KeyBinding("key.blockbuster.fixtures.remove", Keyboard.KEY_NONE, fixtures);

        ClientRegistry.registerKeyBinding(this.editFixture);
        ClientRegistry.registerKeyBinding(this.removeFixture);

        /* Path key bindings */
        this.addPathPoint = new KeyBinding("key.blockbuster.path.add", Keyboard.KEY_NONE, path);
        this.removePathPoint = new KeyBinding("key.blockbuster.path.remove", Keyboard.KEY_NONE, path);

        ClientRegistry.registerKeyBinding(this.addPathPoint);
        ClientRegistry.registerKeyBinding(this.removePathPoint);

        /* General key bindings */
        this.saveProfile = new KeyBinding("key.blockbuster.profile.save", Keyboard.KEY_NONE, general);
        this.loadProfile = new KeyBinding("key.blockbuster.profile.load", Keyboard.KEY_NONE, general);

        ClientRegistry.registerKeyBinding(this.saveProfile);
        ClientRegistry.registerKeyBinding(this.loadProfile);

        this.gotoFixture = new KeyBinding("key.blockbuster.profile.goto", Keyboard.KEY_G, general);
        this.toggleRender = new KeyBinding("key.blockbuster.profile.toggle", Keyboard.KEY_P, general);

        ClientRegistry.registerKeyBinding(this.gotoFixture);
        ClientRegistry.registerKeyBinding(this.toggleRender);

        this.nextFixture = new KeyBinding("key.blockbuster.profile.next", Keyboard.KEY_RBRACKET, general);
        this.prevFixture = new KeyBinding("key.blockbuster.profile.prev", Keyboard.KEY_LBRACKET, general);

        ClientRegistry.registerKeyBinding(this.nextFixture);
        ClientRegistry.registerKeyBinding(this.prevFixture);

        this.startRunning = new KeyBinding("key.blockbuster.profile.start", Keyboard.KEY_Z, general);
        this.stopRunning = new KeyBinding("key.blockbuster.profile.stop", Keyboard.KEY_X, general);

        ClientRegistry.registerKeyBinding(this.startRunning);
        ClientRegistry.registerKeyBinding(this.stopRunning);

        /* Misc */
        this.cameraMarker = new KeyBinding("key.blockbuster.marker", Keyboard.KEY_V, misc);

        ClientRegistry.registerKeyBinding(this.cameraMarker);

        /* Camera key bindings */
        this.increaseDuration = new KeyBinding("key.blockbuster.duration.increase", Keyboard.KEY_NONE, duration);
        this.reduceDuration = new KeyBinding("key.blockbuster.duration.reduce", Keyboard.KEY_NONE, duration);

        ClientRegistry.registerKeyBinding(this.increaseDuration);
        ClientRegistry.registerKeyBinding(this.reduceDuration);

        this.addRoll = new KeyBinding("key.blockbuster.roll.add", Keyboard.KEY_NONE, camera);
        this.reduceRoll = new KeyBinding("key.blockbuster.roll.reduce", Keyboard.KEY_NONE, camera);
        this.resetRoll = new KeyBinding("key.blockbuster.roll.reset", Keyboard.KEY_NONE, camera);

        ClientRegistry.registerKeyBinding(this.addRoll);
        ClientRegistry.registerKeyBinding(this.reduceRoll);
        ClientRegistry.registerKeyBinding(this.resetRoll);

        this.addFov = new KeyBinding("key.blockbuster.fov.add", Keyboard.KEY_NONE, camera);
        this.reduceFov = new KeyBinding("key.blockbuster.fov.reduce", Keyboard.KEY_NONE, camera);
        this.resetFov = new KeyBinding("key.blockbuster.fov.reset", Keyboard.KEY_NONE, camera);

        ClientRegistry.registerKeyBinding(this.addFov);
        ClientRegistry.registerKeyBinding(this.reduceFov);
        ClientRegistry.registerKeyBinding(this.resetFov);
    }

    @SubscribeEvent
    public void onUserLogOut(ClientDisconnectionFromServerEvent event)
    {
        CommandCamera.reset();
        ClientProxy.manager.reset();
        ClientProxy.recordingOverlay.setVisible(false);
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
            L10n.error(player, e.getMessage(), e.getErrorObjects());
        }
    }

    /**
     * Here goes all key bindings that related to camera profile.
     */
    private void handleCameraBindings(EntityPlayer player) throws CommandException
    {
        CameraControl control = CommandCamera.getControl();

        /* Adding fixture */
        int duration = Blockbuster.proxy.config.camera_duration;

        if (this.addIdleFixture.isPressed())
        {
            control.add(player, new IdleFixture(duration));
        }
        else if (this.addPathFixture.isPressed())
        {
            control.add(player, new PathFixture(duration));
        }
        else if (this.addLookFixture.isPressed())
        {
            control.add(player, new LookFixture(duration));
        }
        else if (this.addFollowFixture.isPressed())
        {
            control.add(player, new FollowFixture(duration));
        }
        else if (this.addCircularFixture.isPressed())
        {
            control.add(player, new CircularFixture(1000));
        }

        /* More management */
        if (this.editFixture.isPressed())
        {
            control.edit(player);
        }

        if (this.removeFixture.isPressed())
        {
            control.remove();
        }

        /* Duration management */
        int step = Blockbuster.proxy.config.camera_duration_step;

        if (this.increaseDuration.isKeyDown())
        {
            control.addDuration(step);
        }
        else if (this.reduceDuration.isKeyDown())
        {
            control.addDuration(-step);
        }

        /* Path fixture management */
        if (this.addPathPoint.isPressed())
        {
            control.addPoint(new Position(player));
        }
        else if (this.removePathPoint.isPressed())
        {
            control.removePoint();
        }

        /* Utilities */
        if (this.gotoFixture.isPressed())
        {
            control.goTo(player);
        }

        if (this.toggleRender.isPressed())
        {
            ClientProxy.profileRenderer.toggleRender();
        }

        /* Save and reload */
        if (this.saveProfile.isPressed())
        {
            control.save();
        }
        else if (this.loadProfile.isPressed())
        {
            control.load();
        }

        /* Navigation */
        if (this.nextFixture.isPressed())
        {
            control.next();
        }
        else if (this.prevFixture.isPressed())
        {
            control.prev();
        }

        /* Starting stopping */
        if (this.startRunning.isPressed())
        {
            ClientProxy.profileRunner.start();
        }
        else if (this.stopRunning.isPressed())
        {
            ClientProxy.profileRunner.stop();
        }

        if (this.cameraMarker.isPressed())
        {
            Dispatcher.sendToServer(new PacketCameraMarker());
        }

        if (this.resetRoll.isPressed())
        {
            control.resetRoll();
        }

        if (this.resetFov.isPressed())
        {
            Minecraft.getMinecraft().gameSettings.fovSetting = 70.0F;
        }
    }

    /**
     * Client tick event is used for doing stuff like
     */
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        CameraControl control = CommandCamera.getControl();

        if (this.addRoll.isKeyDown())
        {
            control.addRoll(1.0F);
        }
        else if (this.reduceRoll.isKeyDown())
        {
            control.addRoll(-1.0F);
        }

        if (this.addFov.isKeyDown())
        {
            Minecraft.getMinecraft().gameSettings.fovSetting += 0.25F;
        }
        else if (this.reduceFov.isKeyDown())
        {
            Minecraft.getMinecraft().gameSettings.fovSetting += -0.25F;
        }
    }
}