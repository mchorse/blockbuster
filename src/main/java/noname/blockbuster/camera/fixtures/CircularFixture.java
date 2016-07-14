package noname.blockbuster.camera.fixtures;

import net.minecraft.util.math.Vec3d;

/**
 * Circular camera fixture
 *
 * This camera fixture is responsible for rotating the camera so it would go
 * round in circles relatively given point in space.
 *
 * You know, like one of these rotating thingies on car expos that rotate cars
 * round and round and around...
 */
public class CircularFixture extends AbstractFixture
{
    public Vec3d point;
    public Vec3d start;
    public float circles;

    public CircularFixture(float duration, Vec3d point, Vec3d start, float circles)
    {
        super(duration);

        this.point = point;
        this.start = start;
        this.circles = circles;
    }

    public Vec3d getPoint()
    {
        return this.point;
    }

    public Vec3d getStart()
    {
        return this.start;
    }

    public float getCircles()
    {
        return this.circles;
    }
}
