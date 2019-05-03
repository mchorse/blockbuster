package mchorse.blockbuster.network.common.guns;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketGunInfo implements IMessage
{
    public NBTTagCompound tag;
    public int entity;

    public PacketGunInfo()
    {
        this.tag = new NBTTagCompound();
    }

    public PacketGunInfo(NBTTagCompound tag, int entity)
    {
        this.tag = tag;
        this.entity = entity;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.tag = ByteBufUtils.readTag(buf);
        this.entity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, this.tag);
        buf.writeInt(this.entity);
    }
}