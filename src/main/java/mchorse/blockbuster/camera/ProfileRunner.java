package mchorse.blockbuster.camera;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Profile runner
 *
 * This class is responsible for running camera profiles (i.e. applying current's
 * fixture camera transformations on player).
 */
@SideOnly(Side.CLIENT)
public class ProfileRunner
{
    private Minecraft mc = Minecraft.getMinecraft();
    private float fov = -1;

    protected boolean isRunning = false;
    protected long ticks;
    protected long duration;

    protected CameraProfile profile;
    protected Position position = new Position(0, 0, 0, 0, 0);

    public float yaw = 0.0F;
    public float pitch = 0.0F;

    /* Profile access methods */

    public CameraProfile getProfile()
    {
        return this.profile;
    }

    public void setProfile(CameraProfile profile)
    {
        this.profile = profile;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }

    /* Playback methods (start/stop) */

    /**
     * Start the profile runner. This method also responsible for setting
     * important values before starting the run (like setting duration, and
     * reseting ticks).
     */
    public void start()
    {
        if (this.profile.getCount() == 0)
        {
            return;
        }

        if (!this.isRunning)
        {
            if (Blockbuster.proxy.config.camera_spectator)
            {
                this.mc.player.sendChatMessage("/gamemode 3");
            }

            /* Currently Minema supports client side /minema command which
             * record video */
            if (Blockbuster.proxy.config.camera_minema)
            {
                ClientCommandHandler.instance.executeCommand(this.mc.player, "/minema enable");
            }

            this.fov = this.mc.gameSettings.fovSetting;
            MinecraftForge.EVENT_BUS.register(this);
        }

        this.isRunning = true;
        this.duration = this.profile.getDuration();
        this.ticks = 0;
    }

    public void stop()
    {
        if (this.isRunning)
        {
            if (Blockbuster.proxy.config.camera_spectator)
            {
                this.mc.player.sendChatMessage("/gamemode 1");
            }

            if (Blockbuster.proxy.config.camera_minema)
            {
                ClientCommandHandler.instance.executeCommand(this.mc.player, "/minema disable");
            }

            this.mc.gameSettings.fovSetting = this.fov;
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        this.isRunning = false;

        CommandCamera.getControl().resetRoll();
    }

    /**
     * The method that does the most exciting thing! This method is responsible
     * for applying interpolated fixture on position and apply the output from
     * fixture onto player.
     */
    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event)
    {
        if (event.phase == Phase.START) return;

        long progress = Math.min(this.ticks, this.duration);

        if (progress >= this.duration)
        {
            this.stop();
        }
        else
        {
            this.profile.applyProfile(progress, event.renderTickTime, this.position);

            EntityPlayer player = this.mc.player;
            Point point = this.position.point;
            Angle angle = this.position.angle;

            /* Setting up the camera */
            this.mc.gameSettings.fovSetting = angle.fov;
            CommandCamera.getControl().roll = angle.roll;

            /* Fighting with Optifine disappearing entities bug */
            double y = point.y + Math.sin(progress) * 0.000000001 + 0.000000001;

            player.setLocationAndAngles(point.x, y, point.z, angle.yaw, angle.pitch);
            player.setPositionAndRotation(point.x, y, point.z, angle.yaw, angle.pitch);
            player.motionX = player.motionY = player.motionZ = 0;

            this.yaw = angle.yaw;
            this.pitch = angle.pitch;

            if (player.isSneaking())
            {
                player.setSneaking(false);
            }
        }
    }

    /**
     * This is going to count ticks (used for camera synchronization)
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT && event.player == this.mc.player && event.phase == Phase.START)
        {
            this.ticks++;
        }
    }
}