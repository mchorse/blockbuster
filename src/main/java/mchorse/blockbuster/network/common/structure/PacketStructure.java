package mchorse.blockbuster.network.common.structure;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketStructure implements IMessage
{
    public String name = "";
    public NBTTagCompound tag;

    public PacketStructure()
    {}

    public PacketStructure(String name, NBTTagCompound tag)
    {
        this.name = name;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.name = ByteBufUtils.readUTF8String(buf);
        this.tag = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.name);
        ByteBufUtils.writeTag(buf, this.tag);
    }
}