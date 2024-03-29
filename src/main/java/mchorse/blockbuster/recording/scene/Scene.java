package mchorse.blockbuster.recording.scene;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.audio.AudioHandler;
import mchorse.blockbuster.audio.AudioState;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.scene.fake.FakeContext;
import mchorse.mclib.utils.LatencyTimer;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scene
 */
public class Scene
{
    public static long lastUpdate;

    /**
     * Pattern for finding numbered suffix
     */
    public static final Pattern NUMBERED_SUFFIX = Pattern.compile("_(\\d+)$");

    /**
     * Pattern for finding prefix
     */
    public static final Pattern PREFIX = Pattern.compile("^(.+)_([^_]+)$");

    /**
     * Pattern for finding indexes
     */
    public static final Pattern INDEXES = Pattern.compile("[^_]+");

    /**
     * Scene's id/filename
     */
    private String id = "";

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
     * Whether director's playback is looping
     */
    public boolean loops;

    private AudioHandler audioHandler = new AudioHandler();

    /* Runtime properties */

    /**
     * Whether this scene is active
     */
    public boolean playing;

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
     * Director command sender
     */
    private ICommandSender sender;

    /**
     * This tick used for checking if actors still playing
     */
    private int tick = 0;

    /**
     * Whether this scene gets recorded
     */
    private boolean wasRecording;

    /**
     * Whether it's paused
     */
    private boolean paused;

    /**
     * World instance
     */
    private World world;

    /**
     * List that contains the players that have been chosen for first person playback in this scene
     * with their inventory before playback has been started
     */
    private List<PlayerState> targetPlayers = new ArrayList<>();

    /**
     *
     * @param player
     * @return true if the given player reference has been chosen for first person playback in one actor
     */
    public boolean isPlayerTargetPlayback(EntityPlayer player)
    {
        for (PlayerState state : this.targetPlayers)
        {
            if (state.getPlayer() == player)
            {
                return true;
            }
        }

        return false;
    }

    public List<EntityPlayer> getTargetPlaybackPlayers()
    {
        List<EntityPlayer> players = new ArrayList<>();

        for (PlayerState state : this.targetPlayers)
        {
            players.add(state.getPlayer());
        }

        return players;
    }

    public String getAudio()
    {
        return this.audioHandler.getAudioName();
    }

    public void setAudio(String audio)
    {
        this.audioHandler.setAudioName(audio);
    }

    /**
     * Audio shift
     */
    public int getAudioShift()
    {
        return this.audioHandler.getAudioShift();
    }

    public void setAudioShift(int audioShift)
    {
        this.audioHandler.setAudioShift(audioShift);
        /* update the audio on the server so the shift change is visible */
        if (this.world != null && !this.world.isRemote)
        {
            this.audioHandler.goTo(this.tick);
        }
    }

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public World getWorld()
    {
        return this.world;
    }

    public int getTick()
    {
        return this.tick;
    }

    public int getCurrentTick()
    {
        for (RecordPlayer player : this.actors.values())
        {
            if (!player.isFinished() && !player.actor.isDead)
            {
                return player.tick;
            }
        }

        return 0;
    }

    public AudioState getAudioState()
    {
        return this.audioHandler.getAudioState();
    }

    /**
     * Set director command sender
     */
    public void setSender(ICommandSender sender)
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
                record = CommonProxy.manager.get(replay.id);
            }
            catch (Exception e)
            {}

            if (record != null)
            {
                max = Math.max(max, record.getFullLength());
            }
        }

        return max;
    }

    public void tick()
    {
        if (Blockbuster.debugPlaybackTicks.get())
        {
            this.logTicks();
        }

        for (RecordPlayer player : this.actors.values())
        {
            if (!player.realPlayer && player.actor instanceof EntityPlayer)
            {
                ((EntityPlayerMP) player.actor).onUpdateEntity();
            }
            else if (!player.actorUpdated)
            {
                player.next();
            }

            player.actorUpdated = false;
        }

        if (this.playing && !this.paused)
        {
            if (this.tick % 4 == 0 && !this.checkActors()) return;

            this.audioHandler.update();
            this.tick++;
        }
    }

    public void worldTick(World world)
    {
        for (RecordPlayer player : this.actors.values())
        {
            if (player.actor.world == world)
            {
                if (this.playing)
                {
                    player.checkAndSpawn();
                }

                player.playActions();
            }
        }
    }

    /**
     * Check whether collected actors are still playing
     */
    public boolean areActorsFinished()
    {
        int count = 0;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            RecordPlayer actor = entry.getValue();

            if (this.loops && actor.isFinished())
            {
                actor.record.reset(actor.actor);

                actor.startPlaying(replay.id, actor.kill);
                actor.record.applyAction(0, actor.actor);

                CommonProxy.manager.players.put(actor.actor, actor);
            }

            if ((actor.isFinished() && actor.playing) || actor.actor.isDead)
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
     * @return false if every actor is finished and the scene can be stopped.
     */
    public boolean checkActors()
    {
        /*
         * don't stop the entire scene when one actor is left and if that is the recording actor
         * If it would stop, delayed audio might not start.
         */
        if (this.areActorsFinished() && !this.loops && !this.wasRecording)
        {
            this.stopPlayback(false);

            return false;
        }

        return true;
    }

    /**
     * The same thing as play, but don't play the actor that is passed
     * in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(int tick)
    {
        if (this.getWorld().isRemote || this.playing || this.replays.isEmpty())
        {
            return;
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                RecordUtils.broadcastError("director.empty_filename");

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

        this.playing = true;
        this.sendCommand(this.startCommand);

        if (firstActor != null)
        {
            CommonProxy.damage.addDamageControl(this, firstActor);
        }

        this.audioHandler.startAudio(tick);

        this.wasRecording = false;
        this.paused = false;
        this.tick = tick;
    }

    /**
     * The same thing as play, but don't play the replay that is passed
     * in the arguments (because he might be recorded by the player)
     *
     * Used by recording code.
     */
    public void startPlayback(String exception, int tick)
    {
        if (this.getWorld().isRemote || this.playing)
        {
            return;
        }

        this.collectActors(this.getByFile(exception));

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            RecordPlayer actor = entry.getValue();

            actor.startPlaying(replay.id, tick, true);
        }

        this.playing = true;
        this.sendCommand(this.startCommand);

        this.audioHandler.startAudio(tick);

        this.wasRecording = true;
        this.paused = false;
        this.tick = tick;
    }

    /**
     * Spawns actors at given tick in idle mode and pause the scene. This is pretty useful
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
            this.stopPlayback(true);
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                RecordUtils.broadcastError("director.empty_filename");

                return false;
            }
        }

        this.collectActors(null);
        this.playing = true;
        this.paused = true;

        int j = 0;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            RecordPlayer actor = entry.getValue();

            if (j == 0 && actor.actor != null)
            {
                CommonProxy.damage.addDamageControl(this, actor.actor);
            }

            actor.playing = false;
            actor.startPlaying(replay.id, tick, true);
            actor.sync = true;
            actor.pause();

            for (int i = 0; i <= tick; i++)
            {
                actor.record.applyAction(i - actor.record.preDelay, actor.actor);
            }

            actor.record.applyPreviousMorph(actor.actor, replay, tick, Record.MorphType.PAUSE);

            j++;
        }

        this.audioHandler.pauseAudio(tick);
        this.tick = tick;

        return true;
    }

    /**
     * Force stop playback
     *
     * @param triggered - true if it was caused by something, and false if it just ended playing
     */
    public void stopPlayback(boolean triggered)
    {
        if (!triggered && !this.wasRecording || triggered)
        {
            this.wasRecording = false;
        }

        if (this.getWorld().isRemote || !this.playing)
        {
            return;
        }

        this.tick = 0;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            RecordPlayer actor = entry.getValue();

            actor.kill = true;
            actor.stopPlaying();
        }

        CommonProxy.damage.restoreDamageControl(this, this.getWorld());

        this.targetPlayers.forEach((playerState) ->
        {
            playerState.resetPlayerState();
        });

        this.targetPlayers.clear();

        this.audioHandler.stopAudio();

        this.actors.clear();
        this.playing = false;
        this.sendCommand(this.stopCommand);
    }

    /**
     * Toggle scene's playback
     */
    public boolean togglePlayback()
    {
        if (this.playing)
        {
            this.stopPlayback(true);
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

            World world = this.getWorld();
            EntityLivingBase actor = null;
            boolean real = false;

            /* Locate the target player */
            if (!replay.target.isEmpty())
            {
                EntityPlayerMP player = this.getTargetPlayer(replay.target);

                if (player != null)
                {
                    if (!this.isPlayerTargetPlayback(player))
                    {
                        this.targetPlayers.add(new PlayerState(player));
                    }

                    actor = player;
                    real = true;
                }
            }

            if (actor == null && replay.fake)
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

                IRecording recording = Recording.get(player);

                recording.setFakePlayer(true);

                /* There is no way to construct a CPacketClientSettings on the
                 * server side without using this hack, because the other constructor
                 * is available only on the client side...
                 */
                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(64));

                buffer.writeString("en_US");
                buffer.writeByte((byte) 10);
                buffer.writeEnumValue(EntityPlayer.EnumChatVisibility.FULL);
                buffer.writeBoolean(true);
                buffer.writeByte(127);
                buffer.writeEnumValue(EnumHandSide.RIGHT);

                CPacketClientSettings packet = new CPacketClientSettings();

                try
                {
                    packet.readPacketData(buffer);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                /* Skins layers don't show up by default, for some
                 * reason, thus I have to manually set it myself */
                player.handleClientSettings(packet);
                player.connection = new NetHandlerPlayServer(world.getMinecraftServer(), manager, player);
                actor = player;
            }
            else if (actor == null)
            {
                EntityActor entity = new EntityActor(this.getWorld());

                entity.wasAttached = true;
                actor = entity;
            }

            RecordPlayer player = CommonProxy.manager.play(replay.id, actor, Mode.BOTH, 0, true);

            if (real)
            {
                player.realPlayer();
            }

            if (player != null)
            {
                player.setReplay(replay);

                this.actorsCount++;
                replay.apply(actor);
                this.actors.put(replay, player);
            }
        }

        if (Blockbuster.modelBlockResetOnPlayback.get())
        {
            lastUpdate = System.currentTimeMillis();
        }
    }

    /**
     * Get target player
     */
    private EntityPlayerMP getTargetPlayer(String target)
    {
        PlayerList list = this.world.getMinecraftServer().getPlayerList();

        if (target.equals("@r"))
        {
            /* Pick a random player */
            return list.getPlayers().get((int) (list.getPlayers().size() * Math.random()));
        }
        else if (target.startsWith("@"))
        {
            /* Pick the first player from given team */
            Team team = this.world.getScoreboard().getTeam(target.substring(1));

            if (team != null && !team.getMembershipCollection().isEmpty())
            {
                return list.getPlayerByUsername(team.getMembershipCollection().iterator().next());
            }
        }

        /* Get the player by username */
        return list.getPlayerByUsername(target);
    }

    public boolean isPlaying()
    {
        for (RecordPlayer player : this.actors.values())
        {
            if (player.playing)
            {
                return true;
            }
        }

        return false;
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

        this.audioHandler.pauseAudio();
        this.paused = true;
    }

    /**
     * Resume paused director block playback (basically, resume all actors)
     * @param tick the tick at which to resume playing. If -1 the scene will just play at the tick it was paused at.
     */
    public void resume(int tick)
    {
        if (tick >= 0)
        {
            this.tick = tick;
        }

        for (RecordPlayer player : this.actors.values())
        {
            player.resume(tick);
        }

        this.audioHandler.resume(this.tick);
        this.paused = false;
    }

    /**
     * Make actors go to the given tick
     */
    public void goTo(int tick, boolean actions)
    {
        this.tick = tick;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();

            if (tick == 0)
            {
                replay.apply(entry.getValue().actor);
            }

            entry.getValue().goTo(tick, actions);
        }

        this.audioHandler.goTo(tick);
    }

    /**
     * Reload actors
     */
    public void reload(int tick)
    {
        this.stopPlayback(true);
        this.spawn(tick);
    }

    /**
     * Duplicate
     */
    public boolean dupe(int index)
    {
        if (index < 0 || index >= this.replays.size())
        {
            return false;
        }

        Replay replay = this.replays.get(index).copy();

        replay.id = this.getNextSuffix(replay.id);
        this.replays.add(replay);

        return true;
    }

    /**
     * Return next base suffix, this fixes issue with getNextSuffix() when the
     * scene's name is "tia_6", and it returns "tia_1" instead of "tia_6_1"
     */
    public String getNextBaseSuffix(String filename)
    {
        if (filename.isEmpty())
        {
            return filename;
        }

        return this.getNextSuffix(filename + "_0");
    }

    public String getNextSuffix(String filename)
    {
        if (filename.isEmpty())
        {
            return filename;
        }

        Matcher matcher = NUMBERED_SUFFIX.matcher(filename);

        String prefix = filename;
        boolean found = matcher.find();
        int max = 0;

        if (found)
        {
            prefix = filename.substring(0, matcher.start());
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

        return prefix + "_" + (max + 1);
    }

    public void setupIds()
    {
        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                replay.id = this.getNextBaseSuffix(this.getId());
            }
        }
    }

    public void renamePrefix(String newPrefix)
    {
        this.renamePrefix(null, newPrefix, null);
    }

    public void renamePrefix(@Nullable String oldPrefix, String newPrefix, Function<String, String> process)
    {
        //default format <scene name>_<id>
        for (Replay replay : this.replays)
        {
            Matcher matcher = PREFIX.matcher(replay.id);

            /* test whether <scene name> is at the beginning
            *  and whether there are multiple indexes*/
            if (oldPrefix != null && replay.id.startsWith(oldPrefix+"_"))
            {
                String indexes = replay.id.substring(oldPrefix.length()+1); //length+1 to exclude "_"
                Matcher matcherIndexes = INDEXES.matcher(indexes);

                int counter = 0;

                while (matcherIndexes.find())
                {
                    counter++;
                }

                /* there are mutliple indexes seperated by _ */
                if (counter > 1)
                {
                    replay.id = newPrefix + "_" + indexes;

                    continue;
                }
            }

            if (matcher.find())
            {
                replay.id = newPrefix + "_" + matcher.group(2);
            }
            else if (process != null)
            {
                replay.id = process.apply(replay.id);
            }
        }
    }

    /**
     * Send a command
     */
    public void sendCommand(String command)
    {
        if (this.sender != null && !command.isEmpty())
        {
            this.getWorld().getMinecraftServer().commandManager.executeCommand(this.sender, command);
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

    /**
     * Sends the audio state to the player.
     * @param player
     */
    public void syncAudio(EntityPlayerMP player) {
        this.audioHandler.syncPlayer(player);
    }

    public void copy(Scene scene)
    {
        /* There is no need to copy itself, copying itself will lead to
         * lost of replay data as it clears its replays and then will have
         * nothing to copy over... */
        if (this == scene)
        {
            return;
        }

        this.replays.clear();

        scene.replays.forEach((element) ->
        {
            this.replays.add(element.copy());
        });

        this.loops = scene.loops;
        this.title = scene.title;
        this.startCommand = scene.startCommand;
        this.stopCommand = scene.stopCommand;

        this.audioHandler.copy(scene.audioHandler);
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
        this.title = compound.getString("Title");
        this.startCommand = compound.getString("StartCommand");
        this.stopCommand = compound.getString("StopCommand");

        this.audioHandler.fromNBT(compound);
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
        compound.setString("Title", this.title);
        compound.setString("StartCommand", this.startCommand);
        compound.setString("StopCommand", this.stopCommand);

        this.audioHandler.toNBT(compound);
    }

    /* ByteBuf methods */

    public void fromBuf(ByteBuf buffer)
    {
        this.id = ByteBufUtils.readUTF8String(buffer);
        this.replays.clear();

        for (int i = 0, c = buffer.readInt(); i < c; i++)
        {
            Replay replay = new Replay();

            this.replays.add(replay);
            replay.fromBuf(buffer);
        }

        this.loops = buffer.readBoolean();
        this.title = ByteBufUtils.readUTF8String(buffer);
        this.startCommand = ByteBufUtils.readUTF8String(buffer);
        this.stopCommand = ByteBufUtils.readUTF8String(buffer);

        this.audioHandler.fromBytes(buffer);
    }

    public void toBuf(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.id);
        buffer.writeInt(this.replays.size());

        for (Replay replay : this.replays)
        {
            replay.toBuf(buffer);
        }

        buffer.writeBoolean(this.loops);
        ByteBufUtils.writeUTF8String(buffer, this.title);
        ByteBufUtils.writeUTF8String(buffer, this.startCommand);
        ByteBufUtils.writeUTF8String(buffer, this.stopCommand);

        this.audioHandler.toBytes(buffer);
    }

    /**
     * Class that stores an {@link EntityPlayer} and it's states like inventory etc.
     * This is used for scene first person playback, to reset those players to their
     * original state at the end of playback.
     */
    public class PlayerState
    {
        private EntityPlayer player;
        private NonNullList<ItemStack> mainInventory;
        private int experienceLevel;
        /**
         * The total amount of experience the player has. This also includes the amount of experience within their
         * Experience Bar.
         */
        private int experienceTotal;
        /** The current amount of experience the player has within their Experience Bar. */
        private float experience;
        private int foodLevel;

        public PlayerState(EntityPlayer player)
        {
            this.player = player;

            /* data structure used for mainInventory List in InventoryPlayer class */
            this.mainInventory = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);

            for (int i = 0; i < this.mainInventory.size(); i++)
            {
                this.mainInventory.set(i, player.inventory.mainInventory.get(i).copy());
            }

            this.experience = player.experience;
            this.experienceLevel = player.experienceLevel;
            this.experienceTotal = player.experienceTotal;
            this.foodLevel = player.getFoodStats().getFoodLevel();
        }

        public EntityPlayer getPlayer()
        {
            return this.player;
        }

        /**
         * Resets the player's attributes stored by this state
         */
        public void resetPlayerState()
        {
            for (int i = 0; i < this.player.inventory.mainInventory.size(); i++)
            {
                this.player.inventory.mainInventory.set(i, this.mainInventory.get(i).copy());
            }

            this.player.experience = this.experience;
            this.player.experienceLevel = this.experienceLevel;
            this.player.experienceTotal = this.experienceTotal;
            this.player.getFoodStats().setFoodLevel(this.foodLevel);
        }
    }
}
