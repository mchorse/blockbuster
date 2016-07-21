package noname.blockbuster.camera.fixtures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.camera.Position;

/**
 * Path camera fixture
 *
 * This fixture is responsible for making smooth camera movements through pre
 */
public class PathFixture extends AbstractFixture
{
    protected List<Position> points = new ArrayList<Position>();

    public PathFixture(long duration)
    {
        super(duration);
    }

    public List<Position> getPoints()
    {
        return this.points;
    }

    public void addPoint(Position point)
    {
        this.points.add(point);
    }

    public void removePoint(int index)
    {
        this.points.remove(index);
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {}

    @Override
    public void applyFixture(float progress, Position pos)
    {
        if (this.points.isEmpty()) return;

        progress = progress * (this.points.size() - 1);

        int prev = (int) Math.floor(progress);
        int next = (int) Math.ceil(progress);

        Position prevPos = this.points.get(prev);
        Position nextPos = this.points.get(next);

        progress = progress - prev;

        float x = this.interpolate(prevPos.point.x, nextPos.point.x, progress);
        float y = this.interpolate(prevPos.point.y, nextPos.point.y, progress);
        float z = this.interpolate(prevPos.point.z, nextPos.point.z, progress);

        float yaw = this.interpolate(prevPos.angle.yaw, nextPos.angle.yaw, progress);
        float pitch = this.interpolate(prevPos.angle.pitch, nextPos.angle.pitch, progress);

        pos.point.set(x, y, z);
        pos.angle.set(yaw, pitch);
    }

    private float interpolate(float a, float b, float position)
    {
        return a + (b - a) * position;
    }

    /* Save/load methods */

    @Override
    public byte getType()
    {
        return AbstractFixture.PATH;
    }

    @Override
    public void read(DataInput in) throws IOException
    {
        for (int i = 0, count = in.readInt(); i < count; i++)
        {
            this.addPoint(CameraUtils.readPosition(in));
        }
    }

    @Override
    public void write(DataOutput out) throws IOException
    {
        out.writeInt(this.points.size());

        for (Position point : this.points)
        {
            CameraUtils.writePosition(out, point);
        }
    }

    @Override
    public String toString()
    {
        return I18n.format("blockbuster.fixtures.path", this.points.size(), this.duration);
    }
}
