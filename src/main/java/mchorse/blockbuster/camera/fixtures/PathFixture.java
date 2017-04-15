package mchorse.blockbuster.camera.fixtures;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.Interpolations;
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

    protected InterpolationType interpolationPos;
    protected InterpolationType interpolationAngle;

    public PathFixture(long duration)
    {
        super(duration);

        InterpolationType type = Blockbuster.proxy.config.camera_path_default_interp.equals("cubic") ? InterpolationType.CUBIC : InterpolationType.LINEAR;

        this.interpolationPos = type;
        this.interpolationAngle = type;
    }

    public Position getPoint(int index)
    {
        if (this.points.size() == 0)
        {
            return Position.ZERO;
        }

        if (index >= this.points.size())
        {
            return this.points.get(this.points.size() - 1);
        }

        if (index < 0)
        {
            return this.points.get(0);
        }

        return this.points.get(index);
    }

    public boolean hasPoint(int index)
    {
        return !this.points.isEmpty() && index >= 0 && index < this.points.size();
    }

    public List<Position> getPoints()
    {
        return this.points;
    }

    public void addPoint(Position point)
    {
        this.points.add(point);
    }

    public void addPoint(Position point, int before)
    {
        this.points.add(before, point);
    }

    public void movePoint(int from, int to)
    {
        this.points.add(to, this.points.remove(from));
    }

    public void editPoint(Position point, int index)
    {
        this.points.set(index, point);
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
            if (args[0].equals("linear") || args[0].equals("cubic"))
            {
                InterpolationType type = interpFromString(args[0]);

                this.interpolationPos = type;
                this.interpolationAngle = type;
            }
            else
            {
                int max = this.points.size() - 1;

                this.points.get(CommandBase.parseInt(args[0], 0, max)).set(player);
            }
        }
        else if (args.length == 2)
        {
            if (args[0].equals("linear") || args[0].equals("cubic"))
            {
                this.interpolationPos = interpFromString(args[0]);
            }

            if (args[1].equals("linear") || args[1].equals("cubic"))
            {
                this.interpolationAngle = interpFromString(args[1]);
            }
        }
    }

    @Override
    public void applyFixture(float progress, float partialTicks, Position pos)
    {
        if (this.points.isEmpty()) return;

        int length = this.points.size() - 1;

        progress += ((float) 1 / this.duration) * partialTicks;
        progress = MathHelper.clamp(progress * length, 0, length);

        int index = (int) Math.floor(progress);

        progress = progress - index;

        this.apply(pos, index, progress);
    }

    /**
     * Apply interpolation and stuff
     *
     * Basic if-else, because I'm really lazy to write clever implementation,
     * lol.
     */
    private void apply(Position pos, int index, float progress)
    {
        float x, y, z;
        float yaw, pitch, roll, fov;

        if (this.interpolationPos == null)
        {
            this.interpolationPos = InterpolationType.LINEAR;
        }

        if (this.interpolationAngle == null)
        {
            this.interpolationAngle = InterpolationType.LINEAR;
        }

        Position p0 = this.getPoint(index - 1);
        Position p1 = this.getPoint(index);
        Position p2 = this.getPoint(index + 1);
        Position p3 = this.getPoint(index + 2);

        /* Interpolating the angle */
        if (this.interpolationAngle.equals(InterpolationType.CUBIC))
        {
            yaw = Interpolations.cubicYaw(p0.angle.yaw, p1.angle.yaw, p2.angle.yaw, p3.angle.yaw, progress);
            pitch = Interpolations.cubic(p0.angle.pitch, p1.angle.pitch, p2.angle.pitch, p3.angle.pitch, progress);
            roll = Interpolations.cubic(p0.angle.roll, p1.angle.roll, p2.angle.roll, p3.angle.roll, progress);
            fov = Interpolations.cubic(p0.angle.fov, p1.angle.fov, p2.angle.fov, p3.angle.fov, progress);
        }
        else
        {
            yaw = Interpolations.lerpYaw(p1.angle.yaw, p2.angle.yaw, progress);
            pitch = Interpolations.lerp(p1.angle.pitch, p2.angle.pitch, progress);
            roll = Interpolations.lerp(p1.angle.roll, p2.angle.roll, progress);
            fov = Interpolations.lerp(p1.angle.fov, p2.angle.fov, progress);
        }

        /* Interpolating the position */
        if (this.interpolationPos.equals(InterpolationType.CUBIC))
        {
            x = (float) Interpolations.cubicHermite(p0.point.x, p1.point.x, p2.point.x, p3.point.x, progress);
            y = (float) Interpolations.cubicHermite(p0.point.y, p1.point.y, p2.point.y, p3.point.y, progress);
            z = (float) Interpolations.cubicHermite(p0.point.z, p1.point.z, p2.point.z, p3.point.z, progress);
        }
        else
        {
            x = Interpolations.lerp(p1.point.x, p2.point.x, progress);
            y = Interpolations.lerp(p1.point.y, p2.point.y, progress);
            z = Interpolations.lerp(p1.point.z, p2.point.z, progress);
        }

        pos.point.set(x, y, z);
        pos.angle.set(yaw, pitch, roll, fov);
    }

    /* Save/load methods */

    @Override
    public byte getType()
    {
        return AbstractFixture.PATH;
    }

    @Override
    public void toJSON(JsonObject object)
    {
        object.addProperty("interpolation", this.interpolationPos.equals(InterpolationType.LINEAR) ? "linear" : "cubic");
        object.addProperty("interpolationAngle", this.interpolationAngle.equals(InterpolationType.LINEAR) ? "linear" : "cubic");
    }

    @Override
    public void fromJSON(JsonObject object)
    {
        if (object.has("interpolation"))
        {
            this.interpolationPos = interpFromString(object.get("interpolation").getAsString());
        }

        if (object.has("interpolationAngle"))
        {
            this.interpolationAngle = interpFromString(object.get("interpolationAngle").getAsString());
        }
    }

    public static InterpolationType interpFromString(String string)
    {
        return string.equals("linear") ? InterpolationType.LINEAR : InterpolationType.CUBIC;
    }

    public static enum InterpolationType
    {
        LINEAR, CUBIC;
    }
}