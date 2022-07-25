package mchorse.blockbuster.network.common.audio;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.audio.AudioState;
import mchorse.mclib.utils.LatencyTimer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class PacketAudio implements IMessage
{
    public String audio;
    public AudioState state;
    public int shift;

    /**
     * For syncing purposes to clock the delay
     * between networking and loading the file
     */
    public LatencyTimer delay;

    public PacketAudio()
    {}

    public PacketAudio(String audio, AudioState state, int shift)
    {
        this(audio, state, shift, null);
    }

    public PacketAudio(String audio, AudioState state, int shift, @Nullable LatencyTimer delay)
    {
        this.audio = audio;
        this.state = state;
        this.shift = shift;
        this.delay = delay;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.audio = ByteBufUtils.readUTF8String(buf);
        this.state = AudioState.values()[buf.readInt()];
        this.shift = buf.readInt();

        if (buf.readBoolean())
        {
            this.delay = new LatencyTimer();

            this.delay.fromBytes(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.audio);
        buf.writeInt(this.state.ordinal());
        buf.writeInt(this.shift);

        buf.writeBoolean(this.delay != null);

        if (this.delay != null)
        {
            this.delay.toBytes(buf);
        }
    }
}