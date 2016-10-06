package mchorse.blockbuster.camera.fixtures;

import com.google.gson.annotations.Expose;

import mchorse.blockbuster.camera.Position;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Idle camera fixture
 *
 * This fixture is the basic fixture type. This fixture is responsible for
 * outputting static values for camera.
 */
public class IdleFixture extends AbstractFixture
{
    @Expose
    protected Position position = new Position(0, 0, 0, 0, 0);

    public IdleFixture(long duration)
    {
        super(duration);
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        this.position.set(player);
    }

    @Override
    public void applyFixture(float progress, float partialTicks, Position pos)
    {
        pos.copy(this.position);
    }

    /* Save/load methods */

    @Override
    public byte getType()
    {
        return AbstractFixture.IDLE;
    }
}