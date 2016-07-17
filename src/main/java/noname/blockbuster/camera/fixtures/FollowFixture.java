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
    }

    @Override
    public void applyFixture(float progress, Position pos)
    {
        pos.copy(this.position);
        pos.point.set((float) this.entity.posX + this.position.point.x, (float) this.entity.posY + this.position.point.y, (float) this.entity.posZ + this.position.point.z);
    }
}
