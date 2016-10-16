package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyActor implements IMessage
{
    public int id;
    public String model;
    public ResourceLocation skin;
    public boolean invisible;

    public PacketModifyActor()
    {}

    public PacketModifyActor(int id, String model, ResourceLocation skin, boolean invisible)
    {
        this.id = id;
        this.model = model;
        this.skin = skin;
        this.invisible = invisible;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.model = ByteBufUtils.readUTF8String(buf);
        this.skin = EntityActor.fromString(ByteBufUtils.readUTF8String(buf), this.model);
        this.invisible = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.model);
        ByteBufUtils.writeUTF8String(buf, this.skin == null ? "" : this.skin.toString());
        buf.writeBoolean(this.invisible);
    }
}
