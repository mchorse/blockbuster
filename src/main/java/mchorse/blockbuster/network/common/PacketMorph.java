package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMorph implements IMessage
{
    public String model = "";
    public ResourceLocation skin;

    public PacketMorph()
    {}

    public PacketMorph(String model, ResourceLocation skin)
    {
        this.model = model;
        this.skin = skin;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.model = ByteBufUtils.readUTF8String(buf);
        this.skin = RLUtils.fromString(ByteBufUtils.readUTF8String(buf), this.model);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.model);
        ByteBufUtils.writeUTF8String(buf, this.skin == null ? "" : this.skin.toString());
    }
}
