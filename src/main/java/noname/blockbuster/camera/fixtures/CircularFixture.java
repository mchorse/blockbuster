package noname.blockbuster.camera.fixtures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.camera.Point;
import noname.blockbuster.camera.Position;

/**
 * Circular camera fixture
 *
 * This camera fixture is responsible for rotating the camera so it would go
 * round in circles relatively given point in space.
 *
 * You know, like one of these rotating thingies on car expos that rotate cars
 * round and round and around...
 *
 * @todo Fix this fixture in editing mode
 */
public class CircularFixture extends AbstractFixture
{
    protected Point point = new Point(0, 0, 0);
    protected Point start = new Point(0, 0, 0);
    protected float circles;

    public CircularFixture(long duration)
    {
        super(duration);
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        if (args.length > 0)
        {
            this.circles = (float) CommandBase.parseDouble(args[0]);
        }
    }

    @Override
    public void applyFixture(float progress, Position pos)
    {
        float angle = progress * (this.circles / 180 * (float) Math.PI);

        double diffX = Math.abs(this.point.x - this.start.x);
        double diffZ = Math.abs(this.point.z - this.start.z);

        float dist = (float) Math.sqrt(diffX * diffX + diffZ * diffZ);

        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        float x = this.start.x + dist * cos;
        float y = this.start.y;
        float z = this.start.z + dist * sin;

        float yaw = (float) (MathHelper.atan2(dist * sin, dist * cos) * (180D / Math.PI)) - 90.0F;

        pos.point.set(x - 0.5F, y, z - 0.5F);
        pos.angle.set(MathHelper.wrapDegrees(yaw - 180.0F), 0);
    }

    /* Save/load methods */

    @Override
    public byte getType()
    {
        return AbstractFixture.CIRCULAR;
    }

    @Override
    public void read(DataInput in) throws IOException
    {
        this.circles = in.readFloat();
        this.start = CameraUtils.readPoint(in);
        this.point = CameraUtils.readPoint(in);
    }

    @Override
    public void write(DataOutput out) throws IOException
    {
        out.writeFloat(this.circles);
        CameraUtils.writePoint(out, this.start);
        CameraUtils.writePoint(out, this.point);
    }
}
