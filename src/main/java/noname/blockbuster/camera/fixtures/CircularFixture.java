package noname.blockbuster.camera.fixtures;

import net.minecraft.util.math.MathHelper;
import noname.blockbuster.camera.Point;
import noname.blockbuster.camera.Position;

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
    public Point point;
    public Point start;
    public float circles;

    public CircularFixture(long duration, Point point, Point start, float circles)
    {
        super(duration);

        this.point = point;
        this.start = start;
        this.circles = circles;
    }

    public Point getPoint()
    {
        return this.point;
    }

    public Point getStart()
    {
        return this.start;
    }

    public float getCircles()
    {
        return this.circles;
    }

    @Override
    public void applyFixture(long progress, Position pos)
    {
        float time = ((float) progress) / ((float) this.duration);
        float angle = time * (this.circles / 180 * (float) Math.PI);

        double diffX = Math.abs(this.point.x - this.start.x);
        double diffZ = Math.abs(this.point.z - this.start.z);

        float dist = (float) Math.sqrt(diffX * diffX + diffZ * diffZ);

        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        float x = this.start.x + dist * cos;
        float y = this.start.y;
        float z = this.start.z + dist * sin;

        float yaw = (float) (MathHelper.atan2(dist * sin, dist * cos) * (180D / Math.PI)) - 90.0F;

        pos.setPosition(x - 0.5F, y, z - 0.5F);
        pos.setAngle(MathHelper.wrapDegrees(yaw - 180.0F), 0);
    }
}
