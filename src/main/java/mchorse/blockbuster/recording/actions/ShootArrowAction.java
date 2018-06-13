package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Shoot arrow action
 *
 * This action shoots emulates arrow shooting. This would look confusing when
 * the actor lack of bow, he would be like freaking arrow mage or something.
 */
public class ShootArrowAction extends Action
{
    public int charge;

    public ShootArrowAction()
    {}

    public ShootArrowAction(int charge)
    {
        this.charge = charge;
    }

    @Override
    public byte getType()
    {
        return Action.SHOOT_ARROW;
    }

    /**
     * Some code in this method is borrowed from ItemBow, I guess, I don't
     * remember
     */
    @Override
    public void apply(EntityLivingBase actor)
    {
        World world = actor.world;
        Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();

        EntityTippedArrow arrow = new EntityTippedArrow(world, actor);
        float f = ItemBow.getArrowVelocity(this.charge);

        arrow.shoot(actor, frame.pitch, frame.yaw, 0.0F, f * 3.0F, 1.0F);
        world.spawnEntity(arrow);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.charge = tag.getByte("Charge");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setByte("Charge", (byte) this.charge);
    }
}