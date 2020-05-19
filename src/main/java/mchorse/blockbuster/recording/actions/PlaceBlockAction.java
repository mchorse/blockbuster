package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Guess what this action does
 *
 * Does exactly what you think, no less, no more.
 */
public class PlaceBlockAction extends InteractBlockAction
{
    public byte metadata;
    public String block = "";

    public PlaceBlockAction()
    {}

    public PlaceBlockAction(BlockPos pos, byte metadata, String block)
    {
        super(pos);
        this.metadata = metadata;
        this.block = block;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void apply(EntityLivingBase actor)
    {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(this.block));

        if (block != null)
        {
            IBlockState state = actor.world.getBlockState(this.pos);

            if (InteractBlockAction.BLACKLIST.contains(state.getBlock().getRegistryName()))
            {
                return;
            }

            state = block.getStateFromMeta(this.metadata);
            actor.world.setBlockState(this.pos, state);

            World world = actor.world;

            SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, null);
            world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        }
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.metadata = buf.readByte();
        this.block = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);
        buf.writeByte(this.metadata);
        ByteBufUtils.writeUTF8String(buf, this.block);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);
        this.metadata = tag.getByte("Meta");
        this.block = tag.getString("Block");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);
        tag.setByte("Meta", this.metadata);
        tag.setString("Block", this.block);
    }
}