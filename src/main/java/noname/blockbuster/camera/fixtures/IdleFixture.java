package noname.blockbuster.camera.fixtures;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import noname.blockbuster.camera.Position;

/**
 * Idle camera fixture
 *
 * This fixture is the basic fixture type. This fixture is responsible for
 * outputting static values for camera.
 */
public class IdleFixture extends AbstractFixture
{
    protected Position position;

    public IdleFixture(long duration, Position position)
    {
        super(duration);

        this.position = position;
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        this.position.set(player);
    }

    @Override
    public void applyFixture(float progress, Position pos)
    {
        pos.copy(this.position);
    }
}