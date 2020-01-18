package mchorse.blockbuster.aperture.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlaybackButton implements IMessage
{
    public int mode;
    public String profile;
    public String scene;
    public BlockPos director;

    public PacketPlaybackButton()
    {}

    public PacketPlaybackButton(int mode, String profile, String scene, BlockPos director)
    {
        this.mode = mode;
        this.profile = profile;
        this.scene = scene;
        this.director = director;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.mode = buf.readInt();
        this.profile = ByteBufUtils.readUTF8String(buf);

        if (buf.readBoolean())
        {
            this.scene = ByteBufUtils.readUTF8String(buf);
        }

        if (buf.readBoolean())
        {
            this.director = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.mode);
        ByteBufUtils.writeUTF8String(buf, this.profile);

        buf.writeBoolean(this.scene != null);

        if (this.scene != null)
        {
            ByteBufUtils.writeUTF8String(buf, this.scene);
        }

        buf.writeBoolean(this.director != null);

        if (this.director != null)
        {
            buf.writeInt(this.director.getX());
            buf.writeInt(this.director.getY());
            buf.writeInt(this.director.getZ());
        }
    }
}