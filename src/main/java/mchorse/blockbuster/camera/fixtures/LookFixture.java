package mchorse.blockbuster.camera.fixtures;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Look camera fixture
 *
 * This type of fixture is responsible to transform a camera so it always would
 * be directed towards the given entity.
 */
public class LookFixture extends IdleFixture
{
    protected Entity entity;
    protected String target;

    private float oldYaw = 0;
    private float oldPitch = 0;
    private float oldProgress = 0;

    public LookFixture(long duration)
    {
        super(duration);
    }

    public Entity getTarget()
    {
        return this.entity;
    }

    public void setTarget(String target)
    {
        this.entity = EntityUtils.entityByUUID(Minecraft.getMinecraft().theWorld, target);
        this.target = target;
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        super.edit(args, player);

        Entity target = EntityUtils.getTargetEntity(player);

        if (this.entity == null && target == null)
        {
            throw new CommandException("fixture.no_entity");
        }

        if ((this.entity == null || this.entity.isDead) && target != null)
        {
            this.entity = target;
            this.target = target.getUniqueID().toString();
        }
    }

    /**
     * Totally not taken from EntityLookHelper
     */
    @Override
    public void applyFixture(float progress, float partialTicks, Position pos)
    {
        if (this.entity == null || this.entity.isDead)
        {
            this.tryFindingEntity();

            if (this.entity == null)
            {
                return;
            }
        }

        double x = (this.entity.lastTickPosX + (this.entity.posX - this.entity.lastTickPosX) * partialTicks);
        double y = (this.entity.lastTickPosY + (this.entity.posY - this.entity.lastTickPosY) * partialTicks);
        double z = (this.entity.lastTickPosZ + (this.entity.posZ - this.entity.lastTickPosZ) * partialTicks);

        double dX = x - this.position.point.x;
        double dY = y - this.position.point.y;
        double dZ = z - this.position.point.z;
        double horizontalDistance = MathHelper.sqrt_double(dX * dX + dZ * dZ);

        float yaw = (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI)));

        if (this.oldProgress > progress || this.oldProgress == 0)
        {
            this.oldYaw = yaw;
            this.oldPitch = pitch;
        }

        float value = Blockbuster.proxy.config.camera_interpolate_target ? Blockbuster.proxy.config.camera_interpolate_target_value : 1.0F;

        yaw = this.interpolateYaw(this.oldYaw, yaw, value);
        pitch = this.interpolate(this.oldPitch, pitch, value);

        pos.copy(this.position);
        pos.angle.set(yaw, pitch);

        this.oldYaw = yaw;
        this.oldPitch = pitch;
        this.oldProgress = progress;
    }

    protected void tryFindingEntity()
    {
        this.entity = EntityUtils.entityByUUID(Minecraft.getMinecraft().theWorld, this.target);
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
}