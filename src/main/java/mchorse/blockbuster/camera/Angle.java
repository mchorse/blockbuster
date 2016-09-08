package mchorse.blockbuster.camera;

import com.google.common.base.Objects;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Angle class
 *
 * Don't set yaw and pitch properties directly, unless you're know what you're
 * doing. If yaw is incorrect, you're screwed.
 */
public class Angle
{
    public float yaw;
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
