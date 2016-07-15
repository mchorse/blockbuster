package noname.blockbuster.camera.fixtures;

import net.minecraft.entity.Entity;
import noname.blockbuster.camera.Position;

/**
 * Look camera fixture
 *
 * This type of fixture is responsible to transform a camera so it always would
 * be directed towards the given entity.
 */
public class LookFixture extends IdleFixture
{
    protected Entity entity;

    public LookFixture(long duration, Position position, Entity entity)
    {
        super(duration, position);

        this.entity = entity;
    }

    public Entity getEntity()
    {
        return this.entity;
    }
}
