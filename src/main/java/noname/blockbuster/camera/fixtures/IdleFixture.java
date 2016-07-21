package noname.blockbuster.camera.fixtures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.camera.Position;

/**
 * Idle camera fixture
 *
 * This fixture is the basic fixture type. This fixture is responsible for
 * outputting static values for camera.
 */
public class IdleFixture extends AbstractFixture
{
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
    public void applyFixture(float progress, Position pos)
    {
        pos.copy(this.position);
    }

    /* Save/load methods */

    @Override
    public byte getType()
    {
        return AbstractFixture.IDLE;
    }

    @Override
    public void read(DataInput in) throws IOException
    {
        this.position = CameraUtils.readPosition(in);
    }

    @Override
    public void write(DataOutput out) throws IOException
    {
        CameraUtils.writePosition(out, this.position);
    }

    @Override
    public String toString()
    {
        return I18n.format("blockbuster.fixtures.idle", this.position, this.duration);
    }
}