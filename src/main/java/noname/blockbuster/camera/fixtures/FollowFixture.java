package noname.blockbuster.camera.fixtures;

import net.minecraft.entity.Entity;
import noname.blockbuster.camera.Angle;
import noname.blockbuster.camera.Position;

public class FollowFixture extends LookFixture
{
    protected float distance;
    protected Angle angle;

    public FollowFixture(long duration, Position position, Entity entity, float distance, Angle angle)
    {
        super(duration, position, entity);

        this.distance = distance;
        this.angle = angle;
    }

    public float getDistance()
    {
        return this.distance;
    }

    public Angle getAngle()
    {
        return this.angle;
    }
}
