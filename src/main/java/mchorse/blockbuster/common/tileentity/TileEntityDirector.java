package mchorse.blockbuster.common.tileentity;

import java.util.HashMap;
import java.util.Map;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.recording.Mocap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Director tile entity
 *
 * Used to store actors and manipulate block isPlaying state. Not really sure
 * if it's the best way to implement activation of the redstone (See update
 * method for more information).
 */
public class TileEntityDirector extends AbstractTileEntityDirector
{
    private Map<Replay, EntityActor> actors = new HashMap<Replay, EntityActor>();

    public void add(String id)
    {
        Replay replay = new Replay();
        replay.id = id;

        this.replays.add(replay);
    }

    /* Public API */

    /**
     * Start a playback (make actors play their roles from the files)
     */
    @Override
    public void startPlayback()
    {
        this.startPlayback(null);
    }

    /**
     * The same thing as startPlayback, but don't play the actor that is
     * passed in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(EntityActor exception)
    {
        if (this.worldObj.isRemote || this.isPlaying())
        {
            return;
        }

        this.collectActors();

        System.out.println(this.actors);

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();

            if (actor == exception) continue;

            actor.startPlaying(replay.id);

            if (!replay.actor.equals(actor.getUniqueID()))
            {
                this.worldObj.spawnEntityInWorld(actor);
            }
        }

        this.playBlock(true);
    }

    private void collectActors()
    {
        this.actors.clear();

        for (Replay replay : this.replays)
        {
            EntityActor actor = null;

            if (replay.actor != null)
            {
                actor = (EntityActor) Mocap.entityByUUID(this.worldObj, replay.actor);
            }

            if (actor == null)
            {
                actor = new EntityActor(this.worldObj);
            }

            replay.apply(actor);
            actor.notifyPlayers();
            this.actors.put(replay, actor);
        }
    }

    /**
     * Force stop playback
     */
    @Override
    public void stopPlayback()
    {
        this.stopPlayback(null);
    }

    /**
     * Force stop playback (except one actor)
     */
    public void stopPlayback(EntityActor exception)
    {
        if (this.worldObj.isRemote || !this.isPlaying())
        {
            return;
        }

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();

            if (actor == exception) continue;

            actor.stopPlaying();

            if (!replay.actor.equals(actor.getUniqueID()))
            {
                actor.setDead();
            }
        }

        this.actors.clear();
        this.playBlock(false);
    }

    /**
     * Checks if are actors are still playing. This method gets invoked from
     * abstract parent in the tick method.
     */
    @Override
    protected void areActorsStillPlaying()
    {
        int count = 0;

        for (EntityActor actor : this.actors.values())
        {
            if (!Mocap.playbacks.containsKey(actor)) count++;
        }

        if (count == this.replays.size())
        {
            this.stopPlayback();
            this.playBlock(false);
        }
    }

    public void startRecording(EntityActor actor, EntityPlayer player)
    {
        Replay replay = this.byActor(actor);

        if (replay != null)
        {
            Dispatcher.sendTo(new PacketMorph(replay.model, replay.skin), (EntityPlayerMP) player);
            Mocap.startRecording(replay.id, player);
        }
    }

    private Replay byActor(EntityActor actor)
    {
        for (Replay replay : this.replays)
        {
            if (replay.actor.equals(actor.getUniqueID())) return replay;
        }

        return null;
    }
}