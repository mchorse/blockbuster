package noname.blockbuster.camera;

import com.google.common.base.Objects;

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

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("yaw", this.yaw).add("pitch", this.pitch).toString();
    }
}
