package mchorse.blockbuster.audio;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.audio.PacketAudio;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueString;
import mchorse.mclib.math.functions.limit.Min;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.INBTSerializable;
import mchorse.mclib.utils.ForgeUtils;
import mchorse.mclib.utils.ICopy;
import mchorse.mclib.utils.LatencyTimer;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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
    /**
     * The global tick and not the tick relative to the audio timeline.
     * This is used to determine when to play if there is a negative {@link #audioShift}, which signifies a delay.
     */
    private int tick;

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

    public boolean isPlaying()
    {
        switch (this.audioState)
        {
            case REWIND:
            case RESUME_SET:
            case RESUME:
            case SET:
                return true;
            case PAUSE:
            case STOP:
            case PAUSE_SET:
                return false;
        }

        return false;
    }

    public void pauseAudio()
    {
        this.setAudioStateTick(AudioState.PAUSE, this.tick);
    }

    /**
     * Pause the audio at a certain tick.
     * @param tick The global tick and not the tick relative to the audio timeline.
     *             This needs to be the global tick to determine whether the audio can play due to delayed audio shift.
     */
    public void pauseAudio(int tick)
    {
        this.setAudioStateTick(AudioState.PAUSE_SET, tick);
    }

    /**
     * Resume playing at a certain tick.
     * @param tick the tick to resume playing again at.
     *             The global tick and not the tick relative to the audio timeline.
     *             This needs to be the global tick to determine whether the audio can play due to delayed audio shift.
     */
    public void resume(int tick)
    {
        this.setAudioStateTick(AudioState.RESUME_SET, tick);
    }

    /**
     * Stops the audio.
     */
    public void stopAudio()
    {
        this.audioState = AudioState.STOP;

        this.sendAudioState(AudioState.STOP, Blockbuster.audioSync.get());
    }

    /**
     * Go to a tick. This can happen while in paused/stopped state or in playing state.
     * @param tick the global tick and not the tick relative to the audio timeline.
     *             This needs to be the global tick to determine whether the audio can play due to delayed audio shift.
     */
    public void goTo(int tick)
    {
        /*
         * It is important that the audio state SET is only used when setting the audio while it is playing.
         * AudioState.SET <=> playing
         */
        this.setAudioStateTick(this.isPlaying() ? AudioState.SET : AudioState.PAUSE_SET, tick);
    }

    /**
     *
     * @param tick the global tick and not the tick relative to the audio timeline.
     *             This needs to be the global tick to determine whether the audio can play due to delayed audio shift.
     */
    public void startAudio(int tick)
    {
        this.setAudioStateTick(AudioState.REWIND, tick);
    }

    /**
     * Sets the tick, audiostate and sends them to the players with {@link #sendAudioState(AudioState, boolean)}.
     * If the tick is smaller than the delay, when {@link #audioShift} is negative, it stops the audio.
     * If the tick is greater than the delay it will allow the state to be synchronised.
     * @param state
     * @param tick The global tick and not the tick relative to the audio timeline.
     *             This needs to be the global tick to determine whether the audio can play due to delayed audio shift.
     */
    private void setAudioStateTick(AudioState state, int tick)
    {
        this.tick = tick;

        if (this.audioShift.get() < 0 && tick < -this.audioShift.get())
        {
            this.stopAudio();

            return;
        }

        this.audioState = state;

        this.sendAudioState(state, Blockbuster.audioSync.get());
    }

    /**
     * This method must be called, preferably on the logical server side!
     * This updates the tick and starts the audio if there was a negative {@link #audioShift}.
     * A negative {@link #audioShift} signifies a delay. If the tick is greater than the delay the audio will be started.
     */
    public void update()
    {
        if (this.audioShift.get() < 0 && this.tick >= -this.audioShift.get() && !this.isPlaying())
        {
            this.startAudio(this.tick);
        }

        this.tick++;
    }

    /**
     * Sends the audio, if present, to all players on the server
     * and set {@link #audioState} to the provided state.
     * @param state
     * @param sync whether to try and sync the audio with server and client.
     */
    private void sendAudioState(AudioState state, boolean sync)
    {
        if (!this.hasAudio())
        {
            return;
        }

        for (EntityPlayerMP player : ForgeUtils.getServerPlayers())
        {
            this.sendAudioStateToPlayer(state, (sync) ? new LatencyTimer() : null, player);
        }
    }

    /**
     * Send the current audio state to the player. This is useful when a player joins a server with audio already playing.
     * @param player
     */
    public void syncPlayer(EntityPlayerMP player)
    {
        AudioState state = this.audioState;

        /*
         * convert to SET equivalent states so a player who joins the server
         * gets the audio state and the correct tick set
         */
        switch (this.audioState)
        {
            case PAUSE:
                state = AudioState.PAUSE_SET;
                break;
            case RESUME:
                state = AudioState.RESUME_SET;
                break;
        }

        this.sendAudioStateToPlayer(state, (Blockbuster.audioSync.get()) ? new LatencyTimer() : null, player);
    }

    /**
     * Send the audio to the provided player
     * @param state
     * @param latencyTimer a timer to measure (approximately) the delay to sync the audio properly
     * @param player the entity player the audio should be sent to
     */
    private void sendAudioStateToPlayer(AudioState state, @Nullable LatencyTimer latencyTimer, EntityPlayerMP player)
    {
        if (!this.hasAudio())
        {
            return;
        }

        /**
         * Important: this expects that the calling methods already checked the tick and state
         * so that, for example, an audio with delay of 10 will not try to send a state
         * that will play the audio before tick 10 is reached. If this is violated,
         * the shift passed to {@link AudioLibrary} might become negative.
         */
        int shift = 0;

        switch (state)
        {
            case REWIND:
            case RESUME_SET:
            case PAUSE_SET:
            case SET:
                shift = this.tick;
                break;
            case PAUSE:
            case STOP:
                shift = -this.audioShift.get();
                break;
        }

        PacketAudio packet = new PacketAudio(this.audio.get(), state, shift + this.audioShift.get(), latencyTimer);

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
        this.tick = origin.tick;
    }

    @Override
    public void fromNBT(NBTTagCompound compound)
    {
        this.audio.set(compound.hasKey("Audio") ? compound.getString("Audio") : "");

        if (compound.hasKey("AudioShift"))
        {
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
