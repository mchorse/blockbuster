package mchorse.blockbuster.common.tileentity.director;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.common.tileentity.director.fake.FakeContext;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Director data class
 * 
 * This class is responsible for holding information about replays
 */
public class Director
{
    /**
     * Pattern for finding numbered suffix
     */
    public static final Pattern NUMBERED_SUFFIX = Pattern.compile("_(\\d+)$");

    /**
     * Pattern for finding prefix
     */
    public static final Pattern PREFIX = Pattern.compile("^([\\d\\w\\.]+)_");

    /**
     * List of replays
     */
    public List<Replay> replays = new ArrayList<Replay>();

    /**
     * Display title, used for client organization purposes
     */
    public String title = "";

    /**
     * Command which should be executed when director block starts 
     * playing 
     */
    public String startCommand = "";

    /**
     * Command which should be executed when director block stops 
     * playing  
     */
    public String stopCommand = "";

    /**
     * Whether this director block is playing 
     */
    public boolean playing;

    /**
     * Whether director's playback is looping
     */
    public boolean loops;

    /**
     * Whether director block's state changes are disabled
     */
    public boolean disableStates;

    /**
     * Whether director block should be hidden when playback starts 
     */
    public boolean hide;

    /**
     * Map of currently playing actors 
     */
    public Map<Replay, RecordPlayer> actors = new HashMap<Replay, RecordPlayer>();

    /**
     * Count of actors which were spawned (used to check whether actors 
     * are still playing) 
     */
    public int actorsCount = 0;

    /**
     * Reference to owning tile entity 
     */
    private TileEntityDirector tile;

    /**
     * Director command sender 
     */
    private DirectorSender sender;

    public Director(TileEntityDirector tile)
    {
        this.tile = tile;
    }

    /* Info accessors */

    /**
     * Get tile entity 
     */
    public TileEntityDirector getTile()
    {
        return this.tile;
    }

    /**
     * Set director command sender 
     */
    public void setSender(DirectorSender sender)
    {
        this.sender = sender;
    }

    /**
     * Get a replay by actor. Comparison is based on actor's UUID.
     */
    public Replay getByFile(String filename)
    {
        for (Replay replay : this.replays)
        {
            if (replay.id.equals(filename))
            {
                return replay;
            }
        }

        return null;
    }

    /**
     * Get maximum length of current director block
     */
    public int getMaxLength()
    {
        int max = 0;

        for (Replay replay : this.replays)
        {
            Record record = null;

            try
            {
                record = CommonProxy.manager.getRecord(replay.id);
            }
            catch (Exception e)
            {}

            if (record != null)
            {
                max = Math.max(max, record.getLength());
            }
        }

        return max;
    }

    /**
     * Check whether collected actors are still playing 
     */
    public boolean areActorsPlay()
    {
        int count = 0;

        for (RecordPlayer record : this.actors.values())
        {
            if ((record.isFinished() && record.playing) || record.actor.isDead)
            {
                count++;
            }
        }

        return count == this.actorsCount;
    }

    /* Playback and editing */

    /**
     * Check whether actors are still playing, if they're stop the whole 
     * thing 
     */
    public void checkActors()
    {
        if (this.areActorsPlay())
        {
            if (this.loops)
            {
                /* TODO: improve looping */
                for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
                {
                    Replay replay = entry.getKey();
                    RecordPlayer actor = entry.getValue();
                    boolean notAttached = true;

                    actor.stopPlaying();
                    actor.startPlaying(replay.id, 0, !this.loops);
                }
            }
            else
            {
                this.stopPlayback();
            }
        }
    }

    /**
     * The same thing as startPlayback, but don't play the actor that is passed
     * in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(int tick)
    {
        if (this.tile.getWorld().isRemote || this.playing || this.replays.isEmpty())
        {
            return;
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                Utils.broadcastError("director.empty_filename");

                return;
            }
        }

        this.collectActors(null);

        EntityLivingBase firstActor = null;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            RecordPlayer actor = entry.getValue();

            if (firstActor == null)
            {
                firstActor = actor.actor;
            }

            actor.startPlaying(replay.id, tick, !this.loops);
        }

        this.setPlaying(true);
        this.sendCommand(this.startCommand);

        CommonProxy.manager.addDamageControl(this, firstActor);
    }

    /**
     * The same thing as startPlayback, but don't play the replay that is passed
     * in the arguments (because he might be recorded by the player)
     *
     * Used by recording code.
     */
    public void startPlayback(String exception)
    {
        if (this.tile.getWorld().isRemote || this.playing)
        {
            return;
        }

        this.collectActors(this.getByFile(exception));

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            RecordPlayer actor = entry.getValue();

            actor.startPlaying(replay.id, true);
        }

        this.setPlaying(true);
        this.sendCommand(this.startCommand);
    }

    /**
     * Spawns actors at given tick in idle mode. This is pretty useful 
     * for positioning cameras for exact positions.
     */
    public boolean spawn(int tick)
    {
        if (this.replays.isEmpty())
        {
            return false;
        }

        if (!this.actors.isEmpty())
        {
            this.stopPlayback();
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                Utils.broadcastError("director.empty_filename");

                return false;
            }
        }

        this.collectActors(null);
        this.setPlaying(true);

        int j = 0;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            RecordPlayer actor = entry.getValue();
            boolean notAttached = true;

            if (j == 0)
            {
                CommonProxy.manager.addDamageControl(this, actor.actor);
            }

            actor.playing = false;
            actor.startPlaying(replay.id, tick, notAttached);
            actor.pause();

            for (int i = 0; i <= tick; i++)
            {
                actor.record.applyAction(i, actor.actor);
            }

            j++;
        }

        return true;
    }

    /**
     * Force stop playback (except one actor)
     */
    public void stopPlayback()
    {
        if (this.tile.getWorld().isRemote || !this.playing)
        {
            return;
        }

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            RecordPlayer actor = entry.getValue();

            actor.stopPlaying();
        }

        CommonProxy.manager.restoreDamageControl(this, this.tile.getWorld());

        this.actors.clear();
        this.setPlaying(false);
        this.sendCommand(this.stopCommand);
    }

    /**
     * Toggle scene's playback
     */
    public boolean togglePlayback()
    {
        if (this.playing)
        {
            this.stopPlayback();
        }
        else
        {
            this.startPlayback(0);
        }

        return this.playing;
    }

    /**
     * Collect actors.
     *
     * This method is responsible for collecting actors the ones that in the
     * world and also the ones that doesn't exist (they will be created and
     * spawned later on).
     */
    private void collectActors(Replay exception)
    {
        this.actors.clear();
        this.actorsCount = 0;

        for (Replay replay : this.replays)
        {
            if (replay == exception || !replay.enabled)
            {
                continue;
            }

            World world = this.tile.getWorld();
            EntityLivingBase actor = null;

            if (replay.fake)
            {
                GameProfile profile = new GameProfile(new UUID(0, this.actorsCount), replay.name.isEmpty() ? "Player" : replay.name);

                if (replay.morph instanceof PlayerMorph)
                {
                    profile = ((PlayerMorph) replay.morph).profile;
                }

                EntityPlayerMP player = new EntityPlayerMP(world.getMinecraftServer(), (WorldServer) world, profile, new PlayerInteractionManager(world));
                NetworkManager manager = new NetworkManager(EnumPacketDirection.SERVERBOUND);

                try
                {
                    manager.channelActive(new FakeContext());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                /* Skins layers don't show up by default, for some 
                 * reason, thus I have to manually set it myself */
                player.handleClientSettings(new CPacketClientSettings("en_US", 10, EnumChatVisibility.FULL, true, 127, EnumHandSide.RIGHT));
                player.connection = new NetHandlerPlayServer(world.getMinecraftServer(), manager, player);
                actor = player;
            }
            else
            {
                EntityActor act = new EntityActor(this.tile.getWorld());
                act.wasAttached = true;
                actor = act;
            }

            RecordPlayer player = CommonProxy.manager.startPlayback(replay.id, actor, Mode.BOTH, 0, true, true);

            if (player != null)
            {
                this.actorsCount++;
                replay.apply(actor);
                this.actors.put(replay, player);
            }
        }
    }

    /**
     * Pause the director block playback (basically, pause all actors)
     */
    public void pause()
    {
        for (RecordPlayer actor : this.actors.values())
        {
            actor.pause();
        }
    }

    /**
     * Resume paused director block playback (basically, resume all actors)
     */
    public void resume(int tick)
    {
        for (RecordPlayer actor : this.actors.values())
        {
            actor.resume(tick);
        }
    }

    /**
     * Make actors go to the given tick
     */
    public void goTo(int tick, boolean actions)
    {
        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            if (tick == 0)
            {
                entry.getKey().apply(entry.getValue().actor);
            }

            entry.getValue().goTo(tick, actions);
        }
    }

    /**
     * Duplicate  
     */
    public void dupe(int index, boolean isRemote)
    {
        if (index < 0 || index >= this.replays.size())
        {
            return;
        }

        Replay replay = this.replays.get(index).clone(isRemote);
        Matcher matcher = NUMBERED_SUFFIX.matcher(replay.id);

        String prefix = replay.id;
        boolean found = matcher.find();
        int max = 0;

        if (found)
        {
            prefix = replay.id.substring(0, matcher.start());
        }

        for (Replay other : this.replays)
        {
            if (other.id.startsWith(prefix))
            {
                matcher = NUMBERED_SUFFIX.matcher(other.id);

                if (matcher.find() && other.id.substring(0, matcher.start()).equals(prefix))
                {
                    max = Math.max(max, Integer.parseInt(matcher.group(1)));
                }
            }
        }

        replay.id = prefix + "_" + (max + 1);
        this.replays.add(replay);
    }

    public void renamePrefix(String newPrefix)
    {
        Pattern pattern = Pattern.compile("^([^_]+)_");

        for (Replay replay : this.replays)
        {
            Matcher matcher = pattern.matcher(replay.id);

            if (matcher.find())
            {
                String suffix = replay.id.substring(matcher.end());

                replay.id = newPrefix + "_" + suffix;
            }
        }
    }

    /**
     * Set this director playing
     */
    public void setPlaying(boolean playing)
    {
        this.playing = playing;
        this.tile.playBlock(playing);
    }

    /**
     * Send a command 
     */
    public void sendCommand(String command)
    {
        if (this.sender != null && !command.isEmpty())
        {
            this.tile.getWorld().getMinecraftServer().commandManager.executeCommand(this.sender, command);
        }
    }

    /**
     * Log first actor's ticks (for debug purposes) 
     */
    public void logTicks()
    {
        if (this.actors.isEmpty())
        {
            return;
        }

        RecordPlayer actor = this.actors.values().iterator().next();

        if (actor != null)
        {
            Blockbuster.LOGGER.info("Director tick: " + actor.getTick());
        }
    }

    public void copy(Director director)
    {
        this.replays.clear();
        this.replays.addAll(director.replays);

        this.loops = director.loops;
        this.disableStates = director.disableStates;
        this.hide = director.hide;
        this.title = director.title;
        this.startCommand = director.startCommand;
        this.stopCommand = director.stopCommand;
    }

    /* NBT methods */

    public void fromNBT(NBTTagCompound compound)
    {
        this.replays.clear();

        NBTTagList tagList = compound.getTagList("Actors", 10);

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            Replay replay = new Replay();

            replay.fromNBT(tagList.getCompoundTagAt(i));
            this.replays.add(replay);
        }

        this.loops = compound.getBoolean("Loops");
        this.disableStates = compound.getBoolean("DisableState");
        this.hide = compound.getBoolean("Hide");
        this.title = compound.getString("Title");
        this.startCommand = compound.getString("StartCommand");
        this.stopCommand = compound.getString("StopCommand");
    }

    public void toNBT(NBTTagCompound compound)
    {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < this.replays.size(); i++)
        {
            NBTTagCompound tag = new NBTTagCompound();

            this.replays.get(i).toNBT(tag);
            tagList.appendTag(tag);
        }

        compound.setTag("Actors", tagList);
        compound.setBoolean("Loops", this.loops);
        compound.setBoolean("DisableState", this.disableStates);
        compound.setBoolean("Hide", this.hide);
        compound.setString("Title", this.title);
        compound.setString("StartCommand", this.startCommand);
        compound.setString("StopCommand", this.stopCommand);
    }

    /* ByteBuf methods */

    public void fromBuf(ByteBuf buffer)
    {
        this.replays.clear();

        for (int i = 0, c = buffer.readInt(); i < c; i++)
        {
            Replay replay = new Replay();

            this.replays.add(replay);
            replay.fromBuf(buffer);
        }

        this.loops = buffer.readBoolean();
        this.disableStates = buffer.readBoolean();
        this.hide = buffer.readBoolean();
        this.title = ByteBufUtils.readUTF8String(buffer);
        this.startCommand = ByteBufUtils.readUTF8String(buffer);
        this.stopCommand = ByteBufUtils.readUTF8String(buffer);
    }

    public void toBuf(ByteBuf buffer)
    {
        buffer.writeInt(this.replays.size());

        for (Replay replay : this.replays)
        {
            replay.toBuf(buffer);
        }

        buffer.writeBoolean(this.loops);
        buffer.writeBoolean(this.disableStates);
        buffer.writeBoolean(this.hide);
        ByteBufUtils.writeUTF8String(buffer, this.title);
        ByteBufUtils.writeUTF8String(buffer, this.startCommand);
        ByteBufUtils.writeUTF8String(buffer, this.stopCommand);
    }
}