package noname.blockbuster.recording;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.DimensionManager;
import noname.blockbuster.entity.ActorEntity;

class PlayThread implements Runnable
{
	public Thread thread;
	private ActorEntity replayEntity;
	private DataInputStream in;

	public PlayThread(ActorEntity actor, String filename)
	{
		try
		{
			File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/" + "mocaps");
			in = new DataInputStream(new FileInputStream(file.getAbsolutePath() + "/" + filename + ".mocap"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		replayEntity = actor;
		
		thread = new Thread(this, "Playback Thread");
		thread.start();
	}

	public void run()
	{
		try
		{
			Thread.sleep(500L);
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		try
		{
			short magic = in.readShort();

			if (magic != Mocap.signature)
			{
				throw new Exception("Not a mocap");
			}

			while (true)
			{
				float yaw = in.readFloat();
				float pitch = in.readFloat();
				double x = in.readDouble();
				double y = in.readDouble();
				double z = in.readDouble();
				double mx = in.readDouble();
				double my = in.readDouble();
				double mz = in.readDouble();
				float fd = in.readFloat();
				boolean iab = in.readBoolean();
				boolean isn = in.readBoolean();
				boolean isp = in.readBoolean();
				boolean iog = in.readBoolean();

				replayEntity.isAirBorne = iab;
				replayEntity.motionX = mx;
				replayEntity.motionY = my;
				replayEntity.motionZ = mz;
				replayEntity.fallDistance = fd;
				replayEntity.setSneaking(isn);
				replayEntity.setSprinting(isp);
				replayEntity.onGround = iog;
				replayEntity.setPositionAndRotation(x, y, z, yaw, pitch);
				
				processAction();
				
				Thread.sleep(100L);
			}
		}
		catch (EOFException e)
		{
			System.out.println("Replay thread completed.");
		}
		catch (Exception e)
		{
			Mocap.broadcastMessage("Error loading mocap file, either not a mocap or recorded by an older version.");
			System.out.println("Replay thread interrupted.");
			e.printStackTrace();
		}

		replayEntity.setDead();

		try
		{
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void processAction() throws Exception
	{
		if (!in.readBoolean())
		{
			return;
		}
		
		Action action = new Action(in.readByte());

		switch (action.type)
		{
			case Action.CHAT:
				action.message = in.readUTF();
			break;
			
			case Action.DROP:
				action.itemData = CompressedStreamTools.read(in);
			break;
			
			case Action.EQUIP:
				int aSlot = in.readInt();
				int aId = in.readInt();
				int aDmg = in.readInt();
				
				if (aId != -1) action.itemData = CompressedStreamTools.read(in);
				
				action.armorSlot = aSlot;
				action.armorId = aId;
				action.armorDmg = aDmg;
			break;

			case Action.SHOOTARROW:
				action.arrowCharge = in.readInt();
			break;

			case Action.PLACEBLOCK:
				action.xCoord = in.readInt();
				action.yCoord = in.readInt();
				action.zCoord = in.readInt();
				action.itemData = CompressedStreamTools.read(in);
			break;
		}

		replayEntity.eventsList.add(action);
	}
}
