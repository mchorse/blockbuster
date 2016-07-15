package noname.blockbuster.camera.fixtures;

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

    public CircularFixture(float duration, Point point, Point start, float circles)
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

    }
}
