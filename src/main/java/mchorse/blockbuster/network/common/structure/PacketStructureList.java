package mchorse.blockbuster.network.common.structure;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketStructureList implements IMessage
{
    public List<String> structures;

    public PacketStructureList()
    {
        this.structures = new ArrayList<String>();
    }

    public PacketStructureList(List<String> structures)
    {
        this.structures = structures;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.structures.clear();

        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            this.structures.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.structures.size());

        for (String str : this.structures)
        {
            ByteBufUtils.writeUTF8String(buf, str);
        }
    }
}