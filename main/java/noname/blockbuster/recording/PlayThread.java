package noname.blockbuster.recording;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompressedStreamTools;
import noname.blockbuster.entity.ActorEntity;

/**
 * Play thread
 *
 * This thread is responsible for injecting the movement, rotation, actions, and
 * other stuff into playable actor entity.
 */
class PlayThread implements Runnable
{
    public Thread thread;
    private String filename;
    private ActorEntity actor;
    private DataInputStream in;
    private boolean deadAfterPlay;

    public PlayThread(ActorEntity actor, String filename, boolean deadAfterPlay)
    {
        this.filename = filename;
        this.initStream();

        this.actor = actor;
        this.deadAfterPlay = deadAfterPlay;

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

    @Override
    public void run()
    {
        try
        {
            short magic = this.in.readShort();
            long delay = this.in.readLong();

            if (magic != Mocap.signature)
            {
                throw new Exception("Not a record file");
            }

            while (true)
            {
                this.injectMovement();
                this.injectAction();

                Thread.sleep(delay);
            }
        }
        catch (EOFException e)
        {
            System.out.println("Replay thread completed.");
        }
        catch (Exception e)
        {
            System.out.println("Replay thread interrupted.");
            Mocap.broadcastMessage(I18n.format("blockbuster.mocap.error_file"));
            e.printStackTrace();
        }

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
        float yaw = this.in.readFloat();
        float pitch = this.in.readFloat();
        double x = this.in.readDouble();
        double y = this.in.readDouble();
        double z = this.in.readDouble();
        double mx = this.in.readDouble();
        double my = this.in.readDouble();
        double mz = this.in.readDouble();
        float fd = this.in.readFloat();
        boolean iab = this.in.readBoolean();
        boolean isn = this.in.readBoolean();
        boolean isp = this.in.readBoolean();
        boolean iog = this.in.readBoolean();

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

    /**
     * Inject action into current replay entity
     */
    public void injectAction() throws Exception
    {
        if (!this.in.readBoolean())
        {
            return;
        }

        Action action = new Action(this.in.readByte());

        switch (action.type)
        {
            case Action.CHAT:
                action.message = this.in.readUTF();
                break;

            case Action.DROP:
                action.itemData = CompressedStreamTools.read(this.in);
                break;

            case Action.EQUIP:
                action.armorSlot = this.in.readInt();
                action.armorId = this.in.readInt();
                action.armorDmg = this.in.readInt();

                if (action.armorId != -1)
                    action.itemData = CompressedStreamTools.read(this.in);
                break;

            case Action.SHOOTARROW:
                action.arrowCharge = this.in.readInt();
                break;

            case Action.PLACE_BLOCK:
                action.xCoord = this.in.readInt();
                action.yCoord = this.in.readInt();
                action.zCoord = this.in.readInt();
                action.armorId = this.in.readInt();
                action.armorSlot = this.in.readInt();
                action.itemData = CompressedStreamTools.read(this.in);
                break;

            case Action.MOUNTING:
                action.target = new UUID(this.in.readLong(), this.in.readLong());
                action.armorSlot = this.in.readInt();
                break;

            case Action.INTERACT_BLOCK:
                action.xCoord = this.in.readInt();
                action.yCoord = this.in.readInt();
                action.zCoord = this.in.readInt();
                break;
        }

        this.actor.eventsList.add(action);
    }
}