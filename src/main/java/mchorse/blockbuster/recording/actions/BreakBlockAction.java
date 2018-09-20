package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Breaking block action
 *
 * Actor breaks the block
 */
public class BreakBlockAction extends InteractBlockAction
{
    public boolean drop = false;

    public BreakBlockAction()
    {}

    public BreakBlockAction(BlockPos pos, boolean drop)
    {
        super(pos);
        this.drop = drop;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        actor.world.destroyBlock(this.pos, this.drop);
        actor.world.sendBlockBreakProgress(actor.getEntityId(), this.pos, -1);
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.drop = buf.readBoolean();
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);
        buf.writeBoolean(this.drop);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.drop = tag.getBoolean("Drop");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setBoolean("Drop", this.drop);
    }
}
