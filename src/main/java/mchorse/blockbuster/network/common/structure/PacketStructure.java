package mchorse.blockbuster.network.common.structure;

import io.netty.buffer.ByteBuf;
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

        if (buf.readBoolean())
        {
            this.tag = ByteBufUtils.readTag(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeBoolean(this.tag != null);

        if (this.tag != null)
        {
            ByteBufUtils.writeTag(buf, this.tag);
        }
    }
}