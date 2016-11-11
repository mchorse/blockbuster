package mchorse.blockbuster.camera.fixtures;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.Position;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Follow camera fixture
 *
 * This camera fixture is responsible for following given entity from specified
 * angle and relative calculated position.
 */
public class FollowFixture extends LookFixture
{
    private float oldX = 0;
    private float oldY = 0;
    private float oldZ = 0;
    private float oldProgress = 0;

    public FollowFixture(long duration)
    {
        super(duration);
    }

    @Override
    public void edit(String[] args, EntityPlayer player) throws CommandException
    {
        super.edit(args, player);

        this.calculateRelativePosition();
    }

    /**
     * Following method recalculates relative position from stored entity.
     */
    private void calculateRelativePosition()
    {
        float x = (float) (this.position.point.x - this.entity.posX);
        float y = (float) (this.position.point.y - this.entity.posY);
        float z = (float) (this.position.point.z - this.entity.posZ);

        this.position.point.set(x, y, z);
    }

    @Override
    public void applyFixture(float progress, float partialTicks, Position pos)
    {
        if (this.entity == null)
        {
            this.tryFindingEntity();

            return;
        }

        float x = (float) (this.entity.lastTickPosX + (this.entity.posX - this.entity.lastTickPosX) * partialTicks);
        float y = (float) (this.entity.lastTickPosY + (this.entity.posY - this.entity.lastTickPosY) * partialTicks);
        float z = (float) (this.entity.lastTickPosZ + (this.entity.posZ - this.entity.lastTickPosZ) * partialTicks);

        x += this.position.point.x;
        y += this.position.point.y;
        z += this.position.point.z;

        if (this.oldProgress > progress || this.oldProgress == 0)
        {
            this.oldX = x;
            this.oldY = y;
            this.oldZ = z;
        }

        float value = Blockbuster.proxy.config.camera_interpolate_target ? Blockbuster.proxy.config.camera_interpolate_target_value : 1.0F;

        x = this.interpolate(this.oldX, x, value);
        y = this.interpolate(this.oldY, y, value);
        z = this.interpolate(this.oldZ, z, value);

        pos.copy(this.position);
        pos.point.set(x, y, z);

        this.oldX = x;
        this.oldY = y;
        this.oldZ = z;
        this.oldProgress = progress;
    }

    private float interpolate(float a, float b, float position)
    {
        return a + (b - a) * position;
    }

    @Override
    public byte getType()
    {
        return AbstractFixture.FOLLOW;
    }
}