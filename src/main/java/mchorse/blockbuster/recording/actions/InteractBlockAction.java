package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * Interact block action
 *
 * Makes actor interact with a block (press button, switch lever, open the door,
 * etc.)
 *
 * If there was CL4P-TP actor in this mod, this action would be called
 * IntergradeBlockAction :D
 */
public class InteractBlockAction extends Action
{
    public BlockPos pos;

    public InteractBlockAction()
    {}

    public InteractBlockAction(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public byte getType()
    {
        return Action.INTERACT_BLOCK;
    }

    @Override
    public void apply(EntityActor actor)
    {
        IBlockState state = actor.worldObj.getBlockState(this.pos);

        actor.fakePlayer.posX = actor.posX;
        actor.fakePlayer.posY = actor.posY;
        actor.fakePlayer.posZ = actor.posZ;
        actor.fakePlayer.rotationYaw = actor.rotationYaw;
        actor.fakePlayer.rotationPitch = actor.rotationPitch;

        state.getBlock().onBlockActivated(actor.worldObj, this.pos, state, actor.fakePlayer, EnumHand.MAIN_HAND, null, EnumFacing.UP, this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        super.fromBytes(in);

        this.pos = new BlockPos(in.readInt(), in.readInt(), in.readInt());
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        super.toBytes(out);

        out.writeInt(this.pos.getX());
        out.writeInt(this.pos.getY());
        out.writeInt(this.pos.getZ());
    }
}
