package noname.blockbuster.camera;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.camera.fixtures.IdleFixture;

public class ProfileRunner
{
    protected boolean isRunning;
    protected long startTime;
    protected long duration;

    protected List<AbstractFixture> profile = new ArrayList<AbstractFixture>();
    protected Position position;

    public ProfileRunner()
    {
        this.profile.add(new IdleFixture(1000, new Position(-132, 9, -95, 0, 45)));
        this.profile.add(new IdleFixture(1000, new Position(-126, 9, -95, 90, 45)));

        this.duration = 2000;
        this.position = new Position(0, 0, 0, 0, 0);
    }

    public void start()
    {
        if (this.isRunning) return;

        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void stop()
    {
        if (!this.isRunning) return;

        this.isRunning = false;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event)
    {
        if (!this.isRunning) return;

        long progress = System.currentTimeMillis() - this.startTime;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (progress > this.duration)
        {
            this.stop();
            return;
        }

        AbstractFixture fixture = this.profile.get(this.getIndexForTime(progress));
        fixture.applyFixture(progress, this.position);

        player.setPositionAndRotation(this.position.point.x, this.position.point.y, this.position.point.z, this.position.angle.yaw, this.position.angle.pitch);
    }

    private int getIndexForTime(long progress)
    {
        int index = 0;

        for (AbstractFixture fixture : this.profile)
        {
            progress -= fixture.getDuration();

            if (progress <= 0) break;

            index += 1;
        }

        return index;
    }
}
