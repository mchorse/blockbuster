package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

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
        Block state = actor.worldObj.getBlock(this.pos.x, this.pos.y, this.pos.z);

        /* Black listed block */
        if (state instanceof BlockDirector)
        {
            return;
        }

        Frame frame = actor.playback.record.frames.get(actor.playback.tick);

        actor.fakePlayer.posX = actor.posX;
        actor.fakePlayer.posY = actor.posY;
        actor.fakePlayer.posZ = actor.posZ;
        actor.fakePlayer.rotationYaw = frame.yaw;
        actor.fakePlayer.rotationPitch = frame.pitch;

        state.onBlockActivated(actor.worldObj, this.pos.x, this.pos.y, this.pos.z, actor.fakePlayer, 0, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.pos = new BlockPos(in.readInt(), in.readInt(), in.readInt());
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeInt(this.pos.getX());
        out.writeInt(this.pos.getY());
        out.writeInt(this.pos.getZ());
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.pos = new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setInteger("X", this.pos.getX());
        tag.setInteger("Y", this.pos.getY());
        tag.setInteger("Z", this.pos.getZ());
    }
}