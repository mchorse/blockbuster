package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyModelBlock implements IMessage
{
    public BlockPos pos;
    public TileEntityModel model;
    public boolean merge;

    public PacketModifyModelBlock()
    {}

    public PacketModifyModelBlock(BlockPos pos, TileEntityModel model)
    {
        this.pos = pos;
        this.model = model;
    }

    public PacketModifyModelBlock(BlockPos pos, TileEntityModel model, boolean merge)
    {
        this(pos, model);

        this.merge = merge;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        if (buf.readBoolean())
        {
            this.model = new TileEntityModel();
            this.model.fromBytes(buf);
        }

        this.merge = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeBoolean(this.model != null);

        if (this.model != null)
        {
            this.model.toBytes(buf);
        }

        buf.writeBoolean(this.merge);
    }
}