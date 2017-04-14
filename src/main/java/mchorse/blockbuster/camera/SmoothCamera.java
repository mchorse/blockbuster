package mchorse.blockbuster.camera;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
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

    public float accX;
    public float accY;

    public float fricX = 0.9F;
    public float fricY = 0.9F;

    public void update(EntityPlayer player, float dx, float dy)
    {
        this.accX += dx / 10.0F;
        this.accY += dy / 10.0F;

        this.accX *= this.fricX;
        this.accY *= this.fricY;

        this.yaw += this.accX;
        this.pitch += this.accY;

        if (Blockbuster.proxy.config.camera_smooth_clamp)
        {
            this.pitch = MathHelper.clamp_float(this.pitch, -90, 90);
        }

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
    public class MouseFilter
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