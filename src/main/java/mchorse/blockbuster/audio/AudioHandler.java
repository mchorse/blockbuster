package mchorse.blockbuster.audio;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.audio.PacketAudio;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueString;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.INBTSerializable;
import mchorse.mclib.utils.ICopy;
import mchorse.mclib.utils.LatencyTimer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;

/**
 * This class is responsible for handling an audio, for example, for the scene.
 */
public class AudioHandler implements ICopy<AudioHandler>, INBTSerializable, IByteBufSerializable
{
    /**
     * Cache of the current audio state. This does not need to be serialized
     */
    private AudioState audioState = AudioState.STOP;

    private final ValueInt audioShift = new ValueInt("audio_shift");
    private final ValueString audio = new ValueString("audio_name");

    public String getAudioName()
    {
        return this.audio.get();
    }

    public void setAudioName(String audio)
    {
        this.audio.set((audio == null) ? "" : audio);
    }

    public int getAudioShift()
    {
        return this.audioShift.get();
    }

    public void setAudioShift(int audioShift)
    {
        this.audioShift.set(audioShift);
    }

    public AudioState getAudioState()
    {
        return this.audioState;
    }

    public boolean hasAudio()
    {
        return this.audio.get() != null && !this.audio.get().isEmpty();
    }

    /**
     * Pause audio and synchronise with the clients
     */
    public void pauseAudio(boolean sync)
    {
        this.audioState = AudioState.PAUSE;

        this.sendAudioState(this.audioState, 0, sync);
    }

    public void stopAudio(boolean sync)
    {
        this.audioState = AudioState.STOP;

        this.sendAudioState(this.audioState, 0, sync);
    }

    public void rewindAudio(boolean sync)
    {
        this.audioState = AudioState.REWIND;

        this.sendAudioState(this.audioState, 0, sync);
    }

    /**
     * Send the audio, if present, to all players on the server
     * and set this.audioState to the provided state.
     * @param state
     * @param shift shift the audio in ticks. This is an additional shift,
     *              the audio will also be shifted with the AudioHandler's shift.
     * @param sync whether to try and sync the audio with server and client.
     */
    public void sendAudioState(AudioState state, int shift, boolean sync)
    {
        this.audioState = state;

        if (!this.hasAudio())
        {
            return;
        }

        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getOnlinePlayerNames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            this.sendAudioStateToPlayer(state, shift, (sync) ? new LatencyTimer() : null, player);
        }
    }

    /**
     * Send the audio to the provided player
     * @param state
     * @param shift shift the audio in ticks
     * @param latencyTimer a timer to measure (approximately) the delay to sync the audio properly
     * @param player the entity player the audio should be sent to
     */
    public void sendAudioStateToPlayer(AudioState state, int shift, @Nullable LatencyTimer latencyTimer, EntityPlayerMP player)
    {
        if (!this.hasAudio())
        {
            return;
        }

        PacketAudio packet = new PacketAudio(this.audio.get(), state, this.audioShift.get() + shift, latencyTimer);

        if (player != null)
        {
            Dispatcher.sendTo(packet, player);
        }
    }

    @Override
    public AudioHandler copy()
    {
        AudioHandler clone = new AudioHandler();

        clone.copy(this);

        return clone;
    }

    @Override
    public void copy(AudioHandler origin)
    {
        this.audio.copy(origin.audio);
        this.audioState = origin.audioState;
        this.audioShift.copy(origin.audioShift);
    }

    @Override
    public void fromNBT(NBTTagCompound compound)
    {
        this.audio.set(compound.hasKey("Audio") ? compound.getString("Audio") : "");

        if (compound.hasKey("AudioShift"))
        {
            /* TODO I have no idea why this has something with float, the toNBT method only sends integers... */
            if (compound.getTag("AudioShift") instanceof NBTTagFloat)
            {
                this.audioShift.set((int) (compound.getFloat("AudioShift") * 20));
            }
            else
            {
                this.audioShift.set(compound.getInteger("AudioShift"));
            }
        }
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound compound)
    {
        if (this.audio.hasChanged())
        {
            compound.setTag("Audio", this.audio.valueToNBT());
        }

        if (this.audioShift.hasChanged())
        {
            compound.setTag("AudioShift", this.audioShift.valueToNBT());
        }

        return compound;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        this.audio.fromBytes(byteBuf);
        this.audioShift.fromBytes(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        this.audio.toBytes(byteBuf);
        this.audioShift.toBytes(byteBuf);
    }
}
