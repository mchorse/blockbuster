package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * Guess what this action does
 *
 * Does exactly what you think, no less, no more.
 */
public class PlaceBlockAction extends InteractBlockAction
{
    public byte metadata;
    public String block;

    public PlaceBlockAction()
    {}

    public PlaceBlockAction(BlockPos pos, byte metadata, String block)
    {
        super(pos);
        this.metadata = metadata;
        this.block = block;
    }

    @Override
    public byte getType()
    {
        return Action.PLACE_BLOCK;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void apply(EntityActor actor)
    {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(this.block));
        IBlockState state = block.getStateFromMeta(this.metadata);
        actor.worldObj.setBlockState(this.pos, state);
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        super.fromBytes(in);
        this.metadata = in.readByte();
        this.block = in.readUTF();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        super.toBytes(out);
        out.writeByte(this.metadata);
        out.writeUTF(this.block);
    }
}
