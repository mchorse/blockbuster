package mchorse.blockbuster.recording.director;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.director.fake.FakeContext;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scene
 */
public class Scene
{
	/**
	 * Pattern for finding numbered suffix
	 */
	public static final Pattern NUMBERED_SUFFIX = Pattern.compile("_(\\d+)$");

	/**
	 * Pattern for finding prefix
	 */
	public static final Pattern PREFIX = Pattern.compile("^(.+)_([^_]+)$");

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

	/* Runtime properties */

	/**
	 * Whether this director block is playing
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
	private DirectorSender sender;

	private World world;

	public void setWorld(World world)
	{
		this.world = world;
	}

	public World getWorld()
	{
		return this.world;
	}

	/* Info accessors */

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
				max = Math.max(max, record.getFullLength());
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

		for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
		{
			Replay replay = entry.getKey();
			RecordPlayer actor = entry.getValue();

			if (this.loops && actor.isFinished())
			{
				actor.record.reset(actor.actor);
				actor.tick = 0;
				actor.record.applyFrame(0, actor.actor, true);
				actor.record.applyAction(0, actor.actor);
				Dispatcher.sendToTracked(actor.actor, new PacketPlayback(actor.actor.getEntityId(), true, replay.id));
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
	 */
	public void checkActors()
	{
		if (this.areActorsPlay() && !this.loops)
		{
			this.stopPlayback();
		}
	}

	/**
	 * The same thing as startPlayback, but don't play the actor that is passed
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
		if (this.getWorld().isRemote || this.playing)
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
				actor.record.applyAction(i - actor.record.preDelay, actor.actor);
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
		if (this.getWorld().isRemote || !this.playing)
		{
			return;
		}

		for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
		{
			RecordPlayer actor = entry.getValue();

			actor.kill = true;
			actor.stopPlaying();
		}

		CommonProxy.manager.restoreDamageControl(this, this.getWorld());

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

			World world = this.getWorld();
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

				/* Stupid Mojang/Forge devs made the proper constructor
				 * that allows to pass values into the CPacketClientSettings
				 * client side only, what the fuck? There is no client
				 * side code there, for fuck's sake, why?...
				 */
				PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(64));

				/* I have to use the fucking buffer like a retard in
				 * order to pass the values I wanted to... At least I
				 * didn't had to resort to reflection. Why you have to
				 * be so overprotective of this shit? */
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
			else
			{
				EntityActor act = new EntityActor(this.getWorld());
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
		for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
		{
			entry.getValue().resume(tick, entry.getKey());
		}
	}

	/**
	 * Make actors go to the given tick
	 */
	public void goTo(int tick, boolean actions)
	{
		for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
		{
			Replay replay = entry.getKey();

			if (tick == 0)
			{
				replay.apply(entry.getValue().actor);
			}

			entry.getValue().goTo(tick, actions, replay);
		}
	}

	/**
	 * Duplicate
	 */
	public boolean dupe(int index, boolean isRemote)
	{
		if (index < 0 || index >= this.replays.size())
		{
			return false;
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

		return true;
	}

	public void renamePrefix(String newPrefix)
	{
		for (Replay replay : this.replays)
		{
			Matcher matcher = PREFIX.matcher(replay.id);

			if (matcher.find())
			{
				replay.id = newPrefix + "_" + matcher.group(2);
			}
		}
	}

	/**
	 * Set this director playing
	 */
	public void setPlaying(boolean playing)
	{
		this.playing = playing;
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

	public void copy(Scene scene)
	{
		this.replays.clear();
		this.replays.addAll(scene.replays);

		this.loops = scene.loops;
		this.title = scene.title;
		this.startCommand = scene.startCommand;
		this.stopCommand = scene.stopCommand;
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
		ByteBufUtils.writeUTF8String(buffer, this.title);
		ByteBufUtils.writeUTF8String(buffer, this.startCommand);
		ByteBufUtils.writeUTF8String(buffer, this.stopCommand);
	}
}
