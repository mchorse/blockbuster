package mchorse.blockbuster.client;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.CameraControl;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.camera.SmoothCamera;
import mchorse.blockbuster.camera.fixtures.CircularFixture;
import mchorse.blockbuster.camera.fixtures.FollowFixture;
import mchorse.blockbuster.camera.fixtures.IdleFixture;
import mchorse.blockbuster.camera.fixtures.LookFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.model_editor.GuiModelEditor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketCameraMarker;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.config.Property;
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
    private Minecraft mc = Minecraft.getMinecraft();

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

    public KeyBinding addRoll;
    public KeyBinding reduceRoll;
    private KeyBinding resetRoll;

    public KeyBinding addFov;
    public KeyBinding reduceFov;
    private KeyBinding resetFov;

    /* Camera control */
    private KeyBinding stepUp;
    private KeyBinding stepDown;
    private KeyBinding stepLeft;
    private KeyBinding stepRight;
    private KeyBinding stepFront;
    private KeyBinding stepBack;

    private KeyBinding rotateUp;
    private KeyBinding rotateDown;
    private KeyBinding rotateLeft;
    private KeyBinding rotateRight;

    /* Misc. */
    private KeyBinding modelEditor;
    private KeyBinding cameraMarker;
    private KeyBinding smoothCamera;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        /* Key categories */
        String general = "key.blockbuster.general";
        String fixtures = "key.blockbuster.fixtures.title";
        String camera = "key.blockbuster.camera";
        String control = "key.blockbuster.control.title";
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

        /* Camera control */
        this.stepUp = new KeyBinding("key.blockbuster.control.stepUp", Keyboard.KEY_NONE, control);
        this.stepDown = new KeyBinding("key.blockbuster.control.stepDown", Keyboard.KEY_NONE, control);
        this.stepLeft = new KeyBinding("key.blockbuster.control.stepLeft", Keyboard.KEY_NONE, control);
        this.stepRight = new KeyBinding("key.blockbuster.control.stepRight", Keyboard.KEY_NONE, control);
        this.stepFront = new KeyBinding("key.blockbuster.control.stepFront", Keyboard.KEY_NONE, control);
        this.stepBack = new KeyBinding("key.blockbuster.control.stepBack", Keyboard.KEY_NONE, control);

        ClientRegistry.registerKeyBinding(this.stepUp);
        ClientRegistry.registerKeyBinding(this.stepDown);
        ClientRegistry.registerKeyBinding(this.stepLeft);
        ClientRegistry.registerKeyBinding(this.stepRight);
        ClientRegistry.registerKeyBinding(this.stepFront);
        ClientRegistry.registerKeyBinding(this.stepBack);

        this.rotateUp = new KeyBinding("key.blockbuster.control.rotateUp", Keyboard.KEY_NONE, control);
        this.rotateDown = new KeyBinding("key.blockbuster.control.rotateDown", Keyboard.KEY_NONE, control);
        this.rotateLeft = new KeyBinding("key.blockbuster.control.rotateLeft", Keyboard.KEY_NONE, control);
        this.rotateRight = new KeyBinding("key.blockbuster.control.rotateRight", Keyboard.KEY_NONE, control);

        ClientRegistry.registerKeyBinding(this.rotateUp);
        ClientRegistry.registerKeyBinding(this.rotateDown);
        ClientRegistry.registerKeyBinding(this.rotateLeft);
        ClientRegistry.registerKeyBinding(this.rotateRight);

        /* Misc */
        this.cameraMarker = new KeyBinding("key.blockbuster.marker", Keyboard.KEY_V, misc);
        this.modelEditor = new KeyBinding("key.blockbuster.model_editor", Keyboard.KEY_NONE, misc);
        this.smoothCamera = new KeyBinding("key.blockbuster.smooth_camera", Keyboard.KEY_NONE, misc);

        ClientRegistry.registerKeyBinding(this.cameraMarker);
        ClientRegistry.registerKeyBinding(this.modelEditor);
        ClientRegistry.registerKeyBinding(this.smoothCamera);
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
        EntityPlayer player = Minecraft.getMinecraft().player;

        try
        {
            this.handleCameraBindings(player);
        }
        catch (CommandException e)
        {
            L10n.error(player, e.getMessage(), e.getErrorObjects());
        }

        /* Misc. */
        if (this.cameraMarker.isPressed())
        {
            Dispatcher.sendToServer(new PacketCameraMarker());
        }

        if (this.modelEditor.isPressed())
        {
            this.mc.displayGuiScreen(new GuiModelEditor(false));
        }

        if (this.smoothCamera.isPressed())
        {
            SmoothCamera camera = ClientProxy.profileRenderer.smooth;
            Property enabled = Blockbuster.proxy.forge.getCategory("camera.smooth").get("smooth_enabled");

            enabled.set(!enabled.getBoolean());

            Blockbuster.proxy.onConfigChange(Blockbuster.proxy.forge);
            Blockbuster.proxy.forge.save();

            if (camera.enabled)
            {
                camera.set(player.rotationYaw, -player.rotationPitch);
            }
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

        if (this.resetRoll.isPressed())
        {
            control.resetRoll();
        }

        if (this.resetFov.isPressed())
        {
            control.resetFOV();
        }
    }

    /**
     * Client tick event is used for doing stuff like tick based stuff
     */
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        EntityPlayer player = this.mc.player;

        if (!ClientProxy.profileRenderer.smooth.enabled)
        {
            CameraControl control = CommandCamera.getControl();

            /* Roll key handling */
            if (this.addRoll.isKeyDown())
            {
                control.roll += 0.5F;
            }
            else if (this.reduceRoll.isKeyDown())
            {
                control.roll -= 0.5F;
            }

            /* FOV key handling */
            if (this.addFov.isKeyDown())
            {
                Minecraft.getMinecraft().gameSettings.fovSetting += 0.25F;
            }
            else if (this.reduceFov.isKeyDown())
            {
                Minecraft.getMinecraft().gameSettings.fovSetting += -0.25F;
            }
        }

        /* Camera control keys handling */
        if (player != null)
        {
            double factor = Blockbuster.proxy.config.camera_step_factor;
            double angleFactor = Blockbuster.proxy.config.camera_rotate_factor;

            float yaw = player.rotationYaw;
            float pitch = player.rotationPitch;

            if (this.rotateUp.isKeyDown() || this.rotateDown.isKeyDown())
            {
                pitch += (this.rotateUp.isKeyDown() ? -angleFactor : angleFactor);
            }

            if (this.rotateLeft.isKeyDown() || this.rotateRight.isKeyDown())
            {
                yaw += (this.rotateLeft.isKeyDown() ? -angleFactor : angleFactor);
            }

            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;

            double xx = 0;
            double yy = 0;
            double zz = 0;

            if (this.stepUp.isKeyDown() || this.stepDown.isKeyDown())
            {
                yy = (this.stepUp.isKeyDown() ? factor : -factor);
            }

            if (this.stepLeft.isKeyDown() || this.stepRight.isKeyDown())
            {
                xx = (this.stepLeft.isKeyDown() ? factor : -factor);
            }

            if (this.stepFront.isKeyDown() || this.stepBack.isKeyDown())
            {
                zz = (this.stepFront.isKeyDown() ? factor : -factor);
            }

            if (xx != 0 || yy != 0 || zz != 0 || yaw != player.rotationYaw || pitch != player.rotationPitch)
            {
                Vec3d vec = new Vec3d(xx, yy, zz);

                vec = vec.rotateYaw(-yaw / 180 * (float) Math.PI);

                x += vec.xCoord;
                y += vec.yCoord;
                z += vec.zCoord;

                player.setPositionAndRotation(x, y, z, yaw, pitch);
            }
        }
    }
}