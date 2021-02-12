package mchorse.blockbuster.network.common.audio;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.audio.AudioState;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketAudio implements IMessage
{
    public String audio;
    public AudioState state;
    public int shift;

    public PacketAudio()
    {}

    public PacketAudio(String audio, AudioState state, int shift)
    {
        this.audio = audio;
        this.state = state;
        this.shift = shift;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.audio = ByteBufUtils.readUTF8String(buf);
        this.state = AudioState.values()[buf.readInt()];
        this.shift = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.audio);
        buf.writeInt(this.state.ordinal());
        buf.writeInt(this.shift);
    }
}