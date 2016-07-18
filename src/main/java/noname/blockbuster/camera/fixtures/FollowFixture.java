package noname.blockbuster.camera.fixtures;

import net.minecraft.entity.Entity;
import noname.blockbuster.camera.Position;

/**
 * Follow camera fixture
 *
 * This camera fixture is responsible for following entity
 */
public class FollowFixture extends LookFixture
{
    public FollowFixture(long duration, Position position, Entity entity)
    {
        super(duration, position, entity);

        float x = (float) (position.point.x - entity.posX);
        float y = (float) (position.point.y - entity.posY);
        float z = (float) (position.point.z - entity.posZ);

        this.position.point.set(x, y, z);
    }

    @Override
    public void applyFixture(float progress, Position pos)
    {
        float x = (float) (this.entity.posX + this.position.point.x);
        float y = (float) (this.entity.posY + this.position.point.y);
        float z = (float) (this.entity.posZ + this.position.point.z);

        pos.copy(this.position);
        pos.point.set(x, y, z);
    }
}
