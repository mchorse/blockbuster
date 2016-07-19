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
    protected Point start = new Point(0, 0, 0);
    protected float offset = 0;
    protected float distance = 5;
    protected float circles = 360;

    public CircularFixture(long duration)
    {
        super(duration);
    }

    public float getCircles()
    {
        return this.circles;
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        this.start.x = (float) player.posX;
        this.start.y = (float) player.posY;
        this.start.z = (float) player.posZ;

        this.offset = player.rotationYaw < 0 ? 360 + player.rotationYaw : player.rotationYaw;
        this.offset = (this.offset + 90) % 360;

        if (args.length > 0)
        {
            this.distance = (float) CommandBase.parseDouble(args[0]);
        }

        if (args.length > 1)
        {
            this.circles = (float) CommandBase.parseDouble(args[1]);
        }
    }

    @Override
    public void applyFixture(float progress, Position pos)
    {
        float angle = (this.offset / 180 * (float) Math.PI) + progress * (this.circles / 180 * (float) Math.PI);

        float cos = this.distance * (float) Math.cos(angle);
        float sin = this.distance * (float) Math.sin(angle);

        float x = this.start.x + cos;
        float y = this.start.y;
        float z = this.start.z + sin;

        float yaw = (float) (MathHelper.atan2(sin, cos) * (180D / Math.PI)) - 90.0F;

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
        this.distance = in.readFloat();
        this.circles = in.readFloat();
        this.offset = in.readFloat();
        this.start = CameraUtils.readPoint(in);
    }

    @Override
    public void write(DataOutput out) throws IOException
    {
        out.writeFloat(this.distance);
        out.writeFloat(this.circles);
        out.writeFloat(this.offset);
        CameraUtils.writePoint(out, this.start);
    }
}
