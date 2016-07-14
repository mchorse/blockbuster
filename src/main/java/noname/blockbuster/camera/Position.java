package noname.blockbuster.camera;

import net.minecraft.util.math.Vec3d;

/**
 * Position class
 *
 * This class represents a point in the space with specified angle of view.
 */
public class Position
{
    public Vec3d position;
    public Angle angle = new Angle(0, 0);

    public Position(float x, float y, float z, float yaw, float pitch)
    {
        this.setPosition(x, y, z);
        this.setAngle(yaw, pitch);
    }

    public void setPosition(float x, float y, float z)
    {
        this.position = new Vec3d(x, y, z);
    }

    public void setAngle(float yaw, float pitch)
    {
        this.angle.set(yaw, pitch);
    }
}
