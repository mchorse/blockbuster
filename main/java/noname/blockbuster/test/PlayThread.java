package noname.blockbuster.test;

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
	public Thread t;
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
		
		t = new Thread(this, "Mocap Playback Thread");
		t.start();
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
		boolean hasAction = in.readBoolean();

		if (hasAction)
		{
			byte type = in.readByte();
			Action ma = null;

			switch (type)
			{
				case 1:
					ma = new Action((byte) 1);
					ma.message = in.readUTF();
					break;

				case 4:
					ma = new Action((byte) 4);
					
					int aSlot = in.readInt();
					int aId = in.readInt();
					int aDmg = in.readInt();
					
					if (aId != -1)
					{
						ma.itemData = CompressedStreamTools.read(in);
					}
					ma.armorSlot = aSlot;
					ma.armorId = aId;
					ma.armorDmg = aDmg;
					break;

				case 2:
					ma = new Action((byte) 2);
					break;

				case 3:
					ma = new Action((byte) 3);
					ma.itemData = CompressedStreamTools.read(in);

					break;

				case 5:
					ma = new Action((byte) 5);
					ma.arrowCharge = in.readInt();
					break;

				case 7:
					ma = new Action((byte) 7);
					ma.xCoord = in.readInt();
					ma.yCoord = in.readInt();
					ma.zCoord = in.readInt();
					ma.itemData = CompressedStreamTools.read(in);
					break;
			}

			if (ma != null)
			{
				replayEntity.eventsList.add(ma);
			}
		}
	}
}
