package noname.blockbuster.camera;

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
}
