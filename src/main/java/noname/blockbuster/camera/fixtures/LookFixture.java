package noname.blockbuster.camera.fixtures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.DimensionManager;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.camera.Position;
import noname.blockbuster.recording.Mocap;

/**
 * Look camera fixture
 *
 * This type of fixture is responsible to transform a camera so it always would
 * be directed towards the given entity.
 */
public class LookFixture extends IdleFixture
{
    protected Entity entity;

    private float lastYaw;
    private float lastPitch;

    public LookFixture(long duration)
    {
        super(duration);
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        super.edit(args, player);

        Entity target = CameraUtils.getTargetEntity(player);

        if (this.entity == null && target == null)
        {
            throw new CommandException("blockbuster.fixtures.no_entity");
        }

        if (this.entity == null && target != null)
        {
            this.entity = target;
        }
    }

    /**
     * Totally not taken from EntityLookHelper
     */
    @Override
    public void applyFixture(float progress, Position pos)
    {
        double dX = this.entity.posX - this.position.point.x;
        double dY = this.entity.posY - this.position.point.y;
        double dZ = this.entity.posZ - this.position.point.z;
        double horizontalDistance = MathHelper.sqrt_double(dX * dX + dZ * dZ);

        float yaw = (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI)));

        /* When progress is close to 1.0, the camera starts jagging, so instead
         * of giving the progress to reach its maximum value of 1.0, I decided
         * to limit its maximum to something like 0.5.
         */
        yaw = this.interpolateYaw(this.lastYaw, yaw, progress / 2);
        pitch = this.interpolate(this.lastPitch, pitch, progress / 2);

        pos.copy(this.position);
        pos.angle.set(yaw, pitch);

        this.lastYaw = yaw;
        this.lastPitch = pitch;
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

    private float interpolate(float a, float b, float position)
    {
        return a + (b - a) * position;
    }

    /* Save/load methods */

    @Override
    public byte getType()
    {
        return AbstractFixture.LOOK;
    }

    @Override
    public void read(DataInput in) throws IOException
    {
        super.read(in);

        this.entity = Mocap.entityByUUID(DimensionManager.getWorld(0), in.readUTF());
    }

    @Override
    public void write(DataOutput out) throws IOException
    {
        super.write(out);

        out.writeUTF(this.entity.getUniqueID().toString());
    }
}
