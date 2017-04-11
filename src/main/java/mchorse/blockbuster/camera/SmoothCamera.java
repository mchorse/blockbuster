package mchorse.blockbuster.camera;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Smooth camera
 *
 * This class is responsible for doing cool shit!
 */
@SideOnly(Side.CLIENT)
public class SmoothCamera
{
    public boolean enabled;

    public float yaw;
    public float pitch;

    public MouseFilter x = new MouseFilter();
    public MouseFilter y = new MouseFilter();

    public void update(EntityPlayer player, float dx, float dy)
    {
        this.yaw += dx;
        this.pitch += dy;

        this.x.update(this.yaw);
        this.y.update(this.pitch);
    }

    public void set(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;

        this.x.set(yaw);
        this.y.set(-pitch);
    }

    /**
     * Get interpolated yaw
     */
    public float getInterpYaw(float ticks)
    {
        return Interpolations.cubic(this.x.a, this.x.b, this.x.c, this.x.d, ticks);
    }

    /**
     * Get interpolated pitch
     */
    public float getInterpPitch(float ticks)
    {
        return Interpolations.cubic(this.y.a, this.y.b, this.y.c, this.y.d, ticks);
    }

    /**
     * Just like {@link net.minecraft.util.MouseFilter}, but only uses cubic
     * interolation.
     */
    class MouseFilter
    {
        public float a;
        public float b;
        public float c;
        public float d;

        public void update(float x)
        {
            this.a = x - (x - this.a) * 0.975F;
            this.b = x - (x - this.a) * 0.95F;
            this.c = x - (x - this.a) * 0.90F;
            this.d = x - (x - this.a) * 0.875F;
        }

        public void set(float x)
        {
            this.a = this.b = this.c = this.d = x;
        }
    }
}