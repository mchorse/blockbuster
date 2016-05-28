package noname.blockbuster.recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.DimensionManager;

class RecordThread implements Runnable
{
	public Thread thread;
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

		thread = new Thread(this, "Record Thread");
		thread.start();
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
		for (int i = 1; i < 5; i++)
		{
			int slotIndex = i - 1;

			if (player.inventory.armorInventory[slotIndex] != null)
			{
				if (Item.getIdFromItem(player.inventory.armorInventory[slotIndex].getItem()) != itemsEquipped[i])
				{
					itemsEquipped[i] = Item.getIdFromItem(player.inventory.armorInventory[slotIndex].getItem());
					Action ma = new Action(Action.EQUIP);
					ma.armorSlot = i;
					ma.armorId = itemsEquipped[i];
					ma.armorDmg = player.inventory.armorInventory[slotIndex].getMetadata();

					player.inventory.armorInventory[slotIndex].writeToNBT(ma.itemData);
					eventList.add(ma);
				}
			}
			else if (itemsEquipped[i] != -1)
			{
				itemsEquipped[i] = -1;
				Action ma = new Action(Action.EQUIP);
				ma.armorSlot = i;
				ma.armorId = itemsEquipped[i];
				ma.armorDmg = 0;
				eventList.add(ma);
			}
		}
	}

	/**
	 * Track held item
	 * 
	 * @todo add ability to track also offhand item
	 */
	private void trackHeldItem()
	{
		ItemStack item = player.getHeldItemMainhand();
		
		if (item != null)
		{
			int id = Item.getIdFromItem(item.getItem());
			
			if (id != itemsEquipped[0])
			{
				itemsEquipped[0] = id;
				
				Action ma = new Action(Action.EQUIP);
				ma.armorSlot = 0;
				ma.armorId = itemsEquipped[0];
				ma.armorDmg = item.getMetadata();
				
				item.writeToNBT(ma.itemData);
				eventList.add(ma);
			}
		}
		else if (itemsEquipped[0] != -1)
		{
			itemsEquipped[0] = -1;
			
			Action ma = new Action(Action.EQUIP);
			ma.armorSlot = 0;
			ma.armorId = itemsEquipped[0];
			ma.armorDmg = 0;
			
			eventList.add(ma);
		}
	}

	/**
	 * Track the hand swing (like when you do the tap-tap with left-click)
	 */
	private void trackSwing()
	{
		if (player.isSwingInProgress && !lastTickSwipe)
		{
			lastTickSwipe = true;
			eventList.add(new Action(Action.SWIPE));
		}
		else
		{
			lastTickSwipe = false;
		}
	}

	/**
	 * Write current injected action either via client event handler or action
	 * that was recorded by RecordThread.
	 * 
	 * With enums it looks much much better!
	 */
	private void writeActions() throws IOException
	{
		if (eventList.size() <= 0)
		{
			in.writeBoolean(false);
			return;
		}

		Action ma = eventList.get(0);

		in.writeBoolean(true);
		in.writeByte(ma.type);

		switch (ma.type)
		{
			case Action.CHAT:
				in.writeUTF(ma.message);
			break;

			case Action.DROP:
				CompressedStreamTools.write(ma.itemData, in);
			break;

			case Action.EQUIP:
				in.writeInt(ma.armorSlot);
				in.writeInt(ma.armorId);
				in.writeInt(ma.armorDmg);

				if (ma.armorId != -1) CompressedStreamTools.write(ma.itemData, in);
			break;

			case Action.SHOOTARROW:
				in.writeInt(ma.arrowCharge);
			break;

			case Action.LOGOUT:
				Mocap.recordThreads.remove(player);
				Mocap.broadcastMessage("Stopped recording " + player.getDisplayName().getFormattedText() + ". Bye!");

				capture = false;
			break;

			case Action.PLACEBLOCK:
				in.writeInt(ma.xCoord);
				in.writeInt(ma.yCoord);
				in.writeInt(ma.zCoord);
				CompressedStreamTools.write(ma.itemData, in);
			break;
		}

		eventList.remove(0);
	}
}
