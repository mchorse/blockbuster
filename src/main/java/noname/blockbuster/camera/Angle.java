package noname.blockbuster.camera;

import com.google.common.base.Objects;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Angle class
 *
 * Well, it's kind of obvious what it does, and this comment is absolutely
 * useless. Great job, McHorse, great job!
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
