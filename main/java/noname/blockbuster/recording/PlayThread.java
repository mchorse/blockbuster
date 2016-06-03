package noname.blockbuster.recording;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.nbt.CompressedStreamTools;
import noname.blockbuster.entity.ActorEntity;

class PlayThread implements Runnable
{
    public Thread thread;
    private ActorEntity replayEntity;
    private DataInputStream in;
    private boolean deadAfterPlay;

    public PlayThread(ActorEntity actor, String filename, boolean deadAfterPlay)
    {
        try
        {
            this.in = new DataInputStream(new FileInputStream(Mocap.replayFile(filename)));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        this.replayEntity = actor;
        this.deadAfterPlay = deadAfterPlay;

        this.thread = new Thread(this, "Playback Thread");
        this.thread.start();
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(500L);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

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

                this.replayEntity.isAirBorne = iab;
                this.replayEntity.motionX = mx;
                this.replayEntity.motionY = my;
                this.replayEntity.motionZ = mz;
                this.replayEntity.fallDistance = fd;
                this.replayEntity.setSneaking(isn);
                this.replayEntity.setSprinting(isp);
                this.replayEntity.onGround = iog;
                this.replayEntity.setPositionAndRotation(x, y, z, yaw, pitch);

                this.processAction();

                Thread.sleep(delay);
            }
        }
        catch (EOFException e)
        {
            System.out.println("Replay thread completed.");
        }
        catch (Exception e)
        {
            Mocap.broadcastMessage("Error loading record file, either not a record file or recorded by an older version.");
            System.out.println("Replay thread interrupted.");
            e.printStackTrace();
        }

        if (this.deadAfterPlay)
        {
            this.replayEntity.setDead();
        }

        try
        {
            this.in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void processAction() throws Exception
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
                int aSlot = this.in.readInt();
                int aId = this.in.readInt();
                int aDmg = this.in.readInt();

                if (aId != -1)
                    action.itemData = CompressedStreamTools.read(this.in);

                action.armorSlot = aSlot;
                action.armorId = aId;
                action.armorDmg = aDmg;
                break;

            case Action.SHOOTARROW:
                action.arrowCharge = this.in.readInt();
                break;

            case Action.PLACEBLOCK:
                action.xCoord = this.in.readInt();
                action.yCoord = this.in.readInt();
                action.zCoord = this.in.readInt();
                action.itemData = CompressedStreamTools.read(this.in);
                break;

            case Action.MOUNTING:
                action.target = new UUID(this.in.readLong(), this.in.readLong());
                action.armorSlot = this.in.readInt();
                break;
        }

        this.replayEntity.eventsList.add(action);
    }
}
