package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
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
        IBlockState state = actor.world.getBlockState(this.pos);

        /* Black listed block */
        if (state.getBlock() instanceof BlockDirector)
        {
            return;
        }

        Frame frame = actor.playback.record.frames.get(actor.playback.tick);

        actor.fakePlayer.posX = actor.posX;
        actor.fakePlayer.posY = actor.posY;
        actor.fakePlayer.posZ = actor.posZ;
        actor.fakePlayer.rotationYaw = frame.yaw;
        actor.fakePlayer.rotationPitch = frame.pitch;

        state.getBlock().onBlockActivated(actor.world, this.pos, state, actor.fakePlayer, EnumHand.MAIN_HAND, null, this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    @Override
    public void changeOrigin(double newX, double newY, double newZ, double firstX, double firstY, double firstZ)
    {
        newX += this.pos.getX() - firstX;
        newY += this.pos.getY() - firstY;
        newZ += this.pos.getZ() - firstZ;

        this.pos = new BlockPos(newX, newY, newZ);
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