package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.entity.EntityActor;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemBow;
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
    public void apply(EntityActor actor)
    {
        World world = actor.worldObj;

        EntityTippedArrow arrow = new EntityTippedArrow(world, actor);
        float f = ItemBow.getArrowVelocity(this.charge);

        arrow.setAim(actor, actor.rotationPitch, actor.rotationYaw, 0.0F, f * 3.0F, 1.0F);
        world.spawnEntityInWorld(arrow);
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.charge = in.readInt();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeInt(this.charge);
    }
}
