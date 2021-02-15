package mchorse.blockbuster.network.common.scene;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

public class PacketScenes implements IMessage
{
    public List<String> scenes = new ArrayList<String>();

    public PacketScenes()
    {}

    public PacketScenes(List<String> scenes)
    {
        this.scenes = scenes;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i ++)
        {
            this.scenes.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.scenes.size());

        for (String scene : this.scenes)
        {
            ByteBufUtils.writeUTF8String(buf, scene);
        }
    }
}