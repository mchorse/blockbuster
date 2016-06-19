package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.entity.ActorEntity;

/**
 * Interact block action
 *
 * Makes actor interact with a block (press button, switch lever, open the door,
 * etc.)
 */
public class InteractBlockAction extends Action
{
    public BlockPos pos;

    public InteractBlockAction()
    {
        super(Action.INTERACT_BLOCK);
    }

    public InteractBlockAction(BlockPos pos)
    {
        this();
        this.pos = pos;
    }

    @Override
    public void apply(ActorEntity actor)
    {
        IBlockState state = actor.worldObj.getBlockState(this.pos);

        state.getBlock().onBlockActivated(actor.worldObj, this.pos, state, null, EnumHand.MAIN_HAND, null, EnumFacing.UP, this.pos.getX(), this.pos.getY(), this.pos.getZ());
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
