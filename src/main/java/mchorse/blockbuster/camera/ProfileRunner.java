package mchorse.blockbuster.camera;

import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
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
            this.mc.thePlayer.sendChatMessage("/gamemode 3");
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
            this.mc.thePlayer.sendChatMessage("/gamemode 1");
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

            EntityPlayer player = this.mc.thePlayer;
            Point point = this.position.point;
            Angle angle = this.position.angle;

            this.mc.gameSettings.fovSetting = angle.fov;
            CommandCamera.getControl().roll = angle.roll;

            player.setLocationAndAngles(point.x, point.y, point.z, angle.yaw, angle.pitch);
            player.setPositionAndRotation(point.x, point.y, point.z, angle.yaw, angle.pitch);
            player.motionX = player.motionY = player.motionZ = 0;

            this.ticks++;
        }
    }
}