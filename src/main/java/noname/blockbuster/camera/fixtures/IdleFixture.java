package noname.blockbuster.camera.fixtures;

import com.google.common.base.Objects.ToStringHelper;

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

    public Position getPosition()
    {
        return this.position;
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        super.edit(args, player);
        this.position.set(player);
    }

    @Override
    public void applyFixture(float progress, Position pos)
    {
        pos.copy(this.position);
    }

    @Override
    protected ToStringHelper getToStringHelper()
    {
        return super.getToStringHelper().add("position", this.position);
    }
}