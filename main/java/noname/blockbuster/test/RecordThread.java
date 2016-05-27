package noname.blockbuster.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.DimensionManager;

class RecordThread implements Runnable
{
	public Thread t;
	public boolean capture = false;
	
	private EntityPlayer player;
	private RandomAccessFile in;
	private Boolean lastTickSwipe = Boolean.valueOf(false);
	private int[] itemsEquipped = new int[5];
	private List<Action> eventList;
	
	RecordThread(EntityPlayer _player, String filename)
	{
		try
		{
			File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/" + "mocaps");

			if (!file.exists())
			{
				file.mkdirs();
			}

			in = new RandomAccessFile(file.getAbsolutePath() + "/" + filename + ".mocap", "rw");
			in.setLength(0L);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		player = _player;
		capture = true;
		eventList = Mocap.getActionListForPlayer(player);

		t = new Thread(this, "Mocap Record Thread");
		t.start();
	}

	public void run()
	{
		try
		{
			in.writeShort(Mocap.signature);

			while (capture)
			{
				trackAndWriteMovement();
				trackSwing();
				trackHeldItem();
				trackArmor();
				writeActions();
				Thread.sleep(100L);

				if (player.isDead)
				{
					capture = false;
					Mocap.recordThreads.remove(player);
					Mocap.broadcastMessage("Stopped recording " + player.getDisplayName() + ". RIP.");
				}
			}

			in.close();
		}
		catch (InterruptedException e)
		{
			System.out.println("Child interrupted.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Exiting child thread.");
	}

	/**
	 * Track movement, rotation, and other space control related values
	 */
	private void trackAndWriteMovement() throws IOException
	{
		in.writeFloat(player.rotationYaw);
		in.writeFloat(player.rotationPitch);
		in.writeDouble(player.posX);
		in.writeDouble(player.posY);
		in.writeDouble(player.posZ);
		in.writeDouble(player.motionX);
		in.writeDouble(player.motionY);
		in.writeDouble(player.motionZ);
		in.writeFloat(player.fallDistance);
		in.writeBoolean(player.isAirBorne);
		in.writeBoolean(player.isSneaking());
		in.writeBoolean(player.isSprinting());
		in.writeBoolean(player.onGround);
	}

	/**
	 * Track armor inventory
	 */
	private void trackArmor()
	{
		for (int ci = 1; ci < 5; ci++)
		{
			if (player.inventory.armorInventory[(ci - 1)] != null)
			{
				if (Item.getIdFromItem(player.inventory.armorInventory[(ci - 1)].getItem()) != itemsEquipped[ci])
				{
					itemsEquipped[ci] = Item.getIdFromItem(player.inventory.armorInventory[(ci - 1)].getItem());
					Action ma = new Action((byte) 4);
					ma.armorSlot = ci;
					ma.armorId = itemsEquipped[ci];

					ma.armorDmg = player.inventory.armorInventory[(ci - 1)].getMetadata();
					player.inventory.armorInventory[(ci - 1)].writeToNBT(ma.itemData);
					eventList.add(ma);
				}
			}
			else if (itemsEquipped[ci] != -1)
			{
				itemsEquipped[ci] = -1;
				Action ma = new Action((byte) 4);
				ma.armorSlot = ci;
				ma.armorId = itemsEquipped[ci];
				ma.armorDmg = 0;
				eventList.add(ma);
			}
		}
	}

	private void trackHeldItem()
	{
		if (player.getHeldItemMainhand() != null)
		{
			if (Item.getIdFromItem(player.getHeldItemMainhand().getItem()) != itemsEquipped[0])
			{
				itemsEquipped[0] = Item.getIdFromItem(player.getHeldItemMainhand().getItem());
				Action ma = new Action((byte) 4);
				ma.armorSlot = 0;
				ma.armorId = itemsEquipped[0];
				ma.armorDmg = player.getHeldItemMainhand().getMetadata();
				player.getHeldItemMainhand().writeToNBT(ma.itemData);
				eventList.add(ma);
			}
		}
		else if (itemsEquipped[0] != -1)
		{
			itemsEquipped[0] = -1;
			Action ma = new Action((byte) 4);
			ma.armorSlot = 0;
			ma.armorId = itemsEquipped[0];
			ma.armorDmg = 0;
			eventList.add(ma);
		}
	}

	/**
	 * The hell is that?
	 */
	private void trackSwing()
	{
		if (player.isSwingInProgress && !lastTickSwipe)
		{
			lastTickSwipe = true;
			eventList.add(new Action((byte) 2));
		}
		else
		{
			lastTickSwipe = false;
		}
	}

	/**
	 * Write current injected action either via client event handler or action
	 * that was recorded by RecordThread
	 */
	private void writeActions() throws IOException
	{
		if (eventList.size() > 0)
		{
			Action ma = eventList.get(0);

			in.writeBoolean(true);
			in.writeByte(ma.type);

			switch (ma.type)
			{
				case 1:
					in.writeUTF(ma.message);
					break;

				case 3:
					CompressedStreamTools.write(ma.itemData, in);
					break;

				case 4:
					in.writeInt(ma.armorSlot);
					in.writeInt(ma.armorId);
					in.writeInt(ma.armorDmg);
					if (ma.armorId != -1)
					{
						CompressedStreamTools.write(ma.itemData, in);
					}
					break;

				case 5:
					in.writeInt(ma.arrowCharge);
					break;

				case 6:
					Mocap.recordThreads.remove(player);
					Mocap.broadcastMessage("Stopped recording " + player.getDisplayName() + ". Bye!");
					capture = false;
					break;

				case 7:
					in.writeInt(ma.xCoord);
					in.writeInt(ma.yCoord);
					in.writeInt(ma.zCoord);
					CompressedStreamTools.write(ma.itemData, in);
					break;
			}
			eventList.remove(0);
		}
		else
		{
			in.writeBoolean(false);
		}
	}
}
