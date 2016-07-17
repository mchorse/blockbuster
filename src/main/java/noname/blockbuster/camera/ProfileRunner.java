package noname.blockbuster.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

/**
 * Profile runner
 *
 * This class is responsible for running camera profiles (i.e. applying current's
 * fixture camera transformations on player).
 */
public class ProfileRunner
{
    protected boolean isRunning;
    protected long startTime;
    protected long duration;

    protected CameraProfile profile;
    protected Position position = new Position(0, 0, 0, 0, 0);

    public ProfileRunner(CameraProfile profile)
    {
        this.profile = profile;
    }

    /* Profile access methods */

    public CameraProfile getProfile()
    {
        return this.profile;
    }

    public void setProfile(CameraProfile profile)
    {
        this.profile = profile;
    }

    /* Playback methods (start/stop) */

    public void start()
    {
        if (this.isRunning) return;

        this.isRunning = true;
        this.duration = this.profile.getDuration();
        this.startTime = System.currentTimeMillis();

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void stop()
    {
        if (!this.isRunning) return;

        this.isRunning = false;

        MinecraftForge.EVENT_BUS.unregister(this);
    }

    /**
     * The method that does the most exciting thing! This method is responsible
     * for applying interpolated fixture on position and apply the output from
     * fixture onto player.
     */
    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event)
    {
        if (!this.isRunning) return;

        long progress = System.currentTimeMillis() - this.startTime;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (progress > this.duration)
        {
            this.stop();
        }
        else
        {
            this.profile.applyProfile(progress, this.position);

            player.setPositionAndRotation(this.position.point.x, this.position.point.y, this.position.point.z, this.position.angle.yaw, this.position.angle.pitch);
        }
    }
}
