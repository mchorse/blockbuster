package mchorse.blockbuster.camera;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Angle class
 *
 * Don't set yaw and pitch properties directly, unless you're know what you're
 * doing. If yaw is incorrect, you're screwed.
 */
public class Angle
{
    @Expose
    public float yaw;
    @Expose
    public float pitch;

    public Angle(float yaw, float pitch)
    {
        this.set(yaw, pitch);
    }

    public void set(float yaw, float pitch)
    {
        /* Fix yaw */
        yaw = yaw % 360;
        yaw = yaw > 180 ? -(360 - yaw) : yaw;

        /* Clamp pitch */
        pitch = MathHelper.clamp_float(pitch, -90, 90);

        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void set(EntityPlayer player)
    {
        this.set(player.rotationYaw, player.rotationPitch);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.yaw).addValue(this.pitch).toString();
    }
}