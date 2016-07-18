package noname.blockbuster.camera.fixtures;

import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.camera.Position;

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

    public LookFixture(long duration, Position position, Entity entity)
    {
        super(duration, position);

        this.entity = entity;
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        super.edit(args, player);

        Entity target = CameraUtils.getTargetEntity(player);

        if (target != null) this.entity = target;
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

        if (Math.abs(yaw - this.lastYaw) > 90) this.lastYaw = yaw;
        if (Math.abs(pitch - this.lastPitch) > 90) this.lastPitch = pitch;

        /* When progress is close to 1.0, the camera starts jagging, so instead
         * of giving the progress to reach its maximum value of 1.0, I decided
         * to limit its maximum to something like 0.5.
         */
        yaw = this.interpolate(this.lastYaw, yaw, progress / 2);
        pitch = this.interpolate(this.lastPitch, pitch, progress / 2);

        pos.copy(this.position);
        pos.angle.set(yaw, pitch);

        this.lastYaw = yaw;
        this.lastPitch = pitch;
    }

    private float interpolate(float a, float b, float position)
    {
        return a + (b - a) * position;
    }
}
