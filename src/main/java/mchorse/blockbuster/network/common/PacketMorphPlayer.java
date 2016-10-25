package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMorphPlayer implements IMessage
{
    public int id;
    public String model = "";
    public ResourceLocation skin;

    public PacketMorphPlayer()
    {}

    public PacketMorphPlayer(int id, String model, ResourceLocation skin)
    {
        this.id = id;
        this.model = model;
        this.skin = skin;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.model = ByteBufUtils.readUTF8String(buf);
        this.skin = RLUtils.fromString(ByteBufUtils.readUTF8String(buf), this.model);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.model);
        ByteBufUtils.writeUTF8String(buf, this.skin == null ? "" : this.skin.toString());
    }
}
