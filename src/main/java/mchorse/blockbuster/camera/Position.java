package mchorse.blockbuster.camera;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Position class
 *
 * This class represents a point in the space with specified angle of view.
 */
public class Position
{
    @Expose
    public Point point = new Point(0, 0, 0);
    @Expose
    public Angle angle = new Angle(0, 0);

    public Position(Point point, Angle angle)
    {
        this.point = point;
        this.angle = angle;
    }

    public Position(float x, float y, float z, float yaw, float pitch)
    {
        this.point.set(x, y, z);
        this.angle.set(yaw, pitch);
    }

    public Position(EntityPlayer player)
    {
        this.set(player);
    }

    public void set(EntityPlayer player)
    {
        this.point.set(player);
        this.angle.set(player);
    }

    public void copy(Position position)
    {
        this.point.set(position.point.x, position.point.y, position.point.z);
        this.angle.set(position.angle.yaw, position.angle.pitch, position.angle.roll, position.angle.fov);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.point).addValue(this.angle).toString();
    }
}