package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.world.World;
import noname.blockbuster.entity.ActorEntity;

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

    @Override
    public void apply(ActorEntity actor)
    {
        World world = actor.worldObj;

        EntityTippedArrow arrow = new EntityTippedArrow(world, actor);
        float f = ItemBow.func_185059_b(this.charge);

        arrow.func_184547_a(actor, actor.rotationPitch, actor.rotationYaw, 0.0F, f * 3.0F, 1.0F);
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
