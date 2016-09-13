package mchorse.blockbuster.recording;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

import mchorse.blockbuster.entity.EntityActor;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;

/**
 * Play thread
 *
 * This thread is responsible for injecting the movement, rotation, actions, and
 * other stuff into playable actor entity.
 */
public class PlayThread implements Runnable
{
    public Thread thread;
    public boolean playing;
    public EntityActor actor;

    private String filename;
    private DataInputStream in;
    private boolean deadAfterPlay;

    private long delay;
    private String model;
    private String skin;

    public PlayThread(EntityActor actor, String filename, boolean deadAfterPlay)
    {
        this.filename = filename;
        this.initStream();

        this.actor = actor;
        this.deadAfterPlay = deadAfterPlay;

        this.playing = true;
        this.thread = new Thread(this, "Playback Thread");
        this.thread.start();
    }

    /**
     * Initiate file stream for playback
     */
    private void initStream()
    {
        try
        {
            this.in = new DataInputStream(new FileInputStream(Mocap.replayFile(this.filename)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Run Forest, run!
     *
     * Not really, this method is just responsible for running the whole
     * playback: starting it up, playing, and cleaning up after the playback
     * is finished.
     */
    @Override
    public void run()
    {
        try
        {
            this.start();

            while (this.playing)
            {
                this.next();
                Thread.sleep(this.delay);
            }
        }
        catch (EOFException e)
        {}
        catch (Exception e)
        {
            System.out.println("Replay thread interrupted.");
            e.printStackTrace();
            Mocap.broadcastMessage(I18n.format("blockbuster.mocap.error_file"));
        }

        this.reset();
    }

    /**
     * Start this playback
     *
     * Check if signature matches current's version signature (that's kind of
     * hack-ish) and set the delay between frames.
     */
    public void start() throws Exception
    {
        System.out.println("Replay started.");

        this.model = this.actor.model;
        this.skin = this.actor.skin;

        short magic = this.in.readShort();

        if (magic != Mocap.signature)
        {
            throw new Exception("Not a record file");
        }

        this.delay = this.in.readLong();
    }

    /**
     * Play next frame
     */
    public void next() throws Exception
    {
        this.injectMovement();
        this.injectAction();
    }

    /**
     * Reset the playback
     *
     * Reset entity, remove this playback from Mocap's list of playing
     * threads, and close the file.
     */
    public void reset()
    {
        System.out.println("Replay thread completed.");

        if (this.deadAfterPlay)
        {
            this.actor.setDead();
        }
        else
        {
            this.resetEntity();
        }

        Mocap.playbacks.remove(this.actor);

        try
        {
            this.in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reset the actor entity (put it back on the starting position)
     */
    private void resetEntity()
    {
        this.actor.model = this.model;
        this.actor.skin = this.skin;
        this.actor.notifyPlayers();

        try
        {
            this.in.close();
            this.initStream();
            this.in.skip(10);
            this.injectMovement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Inject movement (position, motion) and rotation into current replay
     * entity (i.e. actor)
     */
    public void injectMovement() throws Exception
    {
        /* Rotation */
        float yaw = this.in.readFloat();
        float pitch = this.in.readFloat();

        /* Position */
        double x = this.in.readDouble();
        double y = this.in.readDouble();
        double z = this.in.readDouble();

        /* Forward & strafe */
        float movef = this.in.readFloat();
        float moves = this.in.readFloat();

        /* Motion */
        double mx = this.in.readDouble();
        double my = this.in.readDouble();
        double mz = this.in.readDouble();

        /* Fall distance */
        float fd = this.in.readFloat();

        /* Booleans */
        boolean iab = this.in.readBoolean();
        boolean isn = this.in.readBoolean();
        boolean isp = this.in.readBoolean();
        boolean iog = this.in.readBoolean();

        if (this.actor.getRidingEntity() == null)
        {
            this.actor.isAirBorne = iab;
            this.actor.motionX = mx;
            this.actor.motionY = my;
            this.actor.motionZ = mz;
            this.actor.fallDistance = fd;
            this.actor.setSneaking(isn);
            this.actor.setSprinting(isp);
            this.actor.onGround = iog;
            this.actor.setPositionAndRotation(x, y, z, yaw, pitch);
        }
        else
        {
            Entity mount = this.actor.getRidingEntity();

            this.actor.rotationYaw = yaw;
            this.actor.rotationPitch = pitch;
            this.actor.moveForward = movef;
            this.actor.moveStrafing = moves;

            mount.isAirBorne = iab;
            mount.motionX = mx;
            mount.motionY = my;
            mount.motionZ = mz;
            mount.fallDistance = fd;
            mount.setSneaking(isn);
            mount.setSprinting(isp);
            mount.onGround = iog;
            mount.setPositionAndRotation(x, y, z, yaw, pitch);
            mount.setRotationYawHead(yaw);
        }
    }

    /**
     * Inject action into current replay entity
     */
    public void injectAction() throws Exception
    {
        if (!this.in.readBoolean())
        {
            return;
        }

        Action action = Action.fromType(this.in.readByte());

        action.fromBytes(this.in);
        this.actor.eventsList.add(action);
    }
}