package noname.blockbuster.camera;

import com.google.common.base.Objects;

/**
 * Position class
 *
 * This class represents a point in the space with specified angle of view.
 */
public class Position
{
    public Point point = new Point(0, 0, 0);
    public Angle angle = new Angle(0, 0);

    public Position(float x, float y, float z, float yaw, float pitch)
    {
        this.setPosition(x, y, z);
        this.setAngle(yaw, pitch);
    }

    public void setPosition(float x, float y, float z)
    {
        this.point.set(x, y, z);
    }

    public void setAngle(float yaw, float pitch)
    {
        this.angle.set(yaw, pitch);
    }

    public void copy(Position position)
    {
        this.point.x = position.point.x;
        this.point.y = position.point.y;
        this.point.z = position.point.z;

        this.angle.yaw = position.angle.yaw;
        this.angle.pitch = position.angle.pitch;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("point", this.point).add("angle", this.angle).toString();
    }
}
