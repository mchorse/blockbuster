package mchorse.blockbuster.camera.fixtures;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import mchorse.blockbuster.camera.Position;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Path camera fixture
 *
 * This fixture is responsible for making smooth camera movements.
 */
public class PathFixture extends AbstractFixture
{
    @Expose
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
    {
        if (args.length == 0)
        {
            this.addPoint(new Position(player));
        }
        else if (args.length == 1)
        {
            int max = this.points.size() - 1;

            this.points.get(CommandBase.parseInt(args[0], 0, max)).set(player);
        }
    }

    @Override
    public void applyFixture(float progress, float partialTicks, Position pos)
    {
        if (this.points.isEmpty()) return;

        int length = this.points.size() - 1;

        progress += ((float) 1 / this.duration) * partialTicks;
        progress = MathHelper.clamp_float(progress * (length), 0, length);

        int prev = (int) Math.floor(progress);
        int next = (int) Math.ceil(progress);

        Position prevPos = this.points.get(prev);
        Position nextPos = this.points.get(next);

        progress = progress - prev;

        float x = this.interpolate(prevPos.point.x, nextPos.point.x, progress);
        float y = this.interpolate(prevPos.point.y, nextPos.point.y, progress);
        float z = this.interpolate(prevPos.point.z, nextPos.point.z, progress);

        float yaw = this.interpolateYaw(prevPos.angle.yaw, nextPos.angle.yaw, progress);
        float pitch = this.interpolate(prevPos.angle.pitch, nextPos.angle.pitch, progress);
        float roll = this.interpolate(prevPos.angle.roll, nextPos.angle.roll, progress);
        float fov = this.interpolate(prevPos.angle.fov, nextPos.angle.fov, progress);

        pos.point.set(x, y, z);
        pos.angle.set(yaw, pitch, roll, fov);
    }

    private float interpolate(float a, float b, float position)
    {
        return a + (b - a) * position;
    }

    /**
     * Special interpolation method for interpolating yaw. The problem with yaw,
     * is that it may go in the "wrong" direction when having, for example,
     * -170 (as a) and 170 (as b) degress or other way around (170 and -170).
     *
     * This interpolation method fixes this problem.
     */
    private float interpolateYaw(float a, float b, float position)
    {
        float diff = b - a;

        if (diff > 180 || diff < -180)
        {
            diff = Math.copySign(360 - Math.abs(diff), -diff);
            float value = a + diff * position;

            return value > 180 ? -(360 - value) : (value < -180 ? 360 + value : value);
        }

        return a + (b - a) * position;
    }

    /* Save/load methods */

    @Override
    public byte getType()
    {
        return AbstractFixture.PATH;
    }
}