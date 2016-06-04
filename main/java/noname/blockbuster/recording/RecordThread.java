package noname.blockbuster.recording;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;

class RecordThread implements Runnable
{
    public Thread thread;
    public boolean capture = false;

    private EntityPlayer player;
    private RandomAccessFile in;
    private Boolean lastTickSwipe = Boolean.valueOf(false);
    private int[] itemsEquipped = new int[6];
    private List<Action> eventList;

    RecordThread(EntityPlayer _player, String filename)
    {
        try
        {
            this.in = new RandomAccessFile(Mocap.replayFile(filename), "rw");
            this.in.setLength(0L);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.player = _player;
        this.capture = true;
        this.eventList = Mocap.getActionListForPlayer(this.player);

        this.thread = new Thread(this, "Record Thread");
        this.thread.start();
    }

    @Override
    public void run()
    {
        try
        {
            this.in.writeShort(Mocap.signature);
            this.in.writeLong(Mocap.delay);

            while (this.capture)
            {
                this.trackAndWriteMovement();
                this.trackSwing();
                this.trackHeldItem();
                this.trackArmor();
                this.writeActions();

                Thread.sleep(Mocap.delay);

                if (this.player.isDead)
                {
                    this.capture = false;

                    Mocap.records.remove(this.player);
                    Mocap.broadcastMessage("Stopped recording " + this.player.getDisplayName().getFormattedText() + ". RIP.");
                }
            }

            this.in.close();
        }
        catch (InterruptedException e)
        {
            System.out.println("Child interrupted.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Exiting record thread.");
    }

    /**
     * Track movement, rotation, and other space control related values
     */
    private void trackAndWriteMovement() throws IOException
    {
        this.in.writeFloat(this.player.rotationYaw);
        this.in.writeFloat(this.player.rotationPitch);
        this.in.writeDouble(this.player.posX);
        this.in.writeDouble(this.player.posY);
        this.in.writeDouble(this.player.posZ);
        this.in.writeDouble(this.player.motionX);
        this.in.writeDouble(this.player.motionY);
        this.in.writeDouble(this.player.motionZ);
        this.in.writeFloat(this.player.fallDistance);
        this.in.writeBoolean(this.player.isAirBorne);
        this.in.writeBoolean(this.player.isSneaking());
        this.in.writeBoolean(this.player.isSprinting());
        this.in.writeBoolean(this.player.onGround);
    }

    /**
     * Track armor inventory
     */
    private void trackArmor()
    {
        for (int i = 1; i < 5; i++)
        {
            int slotIndex = i - 1;

            if (this.player.inventory.armorInventory[slotIndex] != null)
            {
                if (Item.getIdFromItem(this.player.inventory.armorInventory[slotIndex].getItem()) != this.itemsEquipped[i])
                {
                    this.itemsEquipped[i] = Item.getIdFromItem(this.player.inventory.armorInventory[slotIndex].getItem());
                    Action ma = new Action(Action.EQUIP);
                    ma.armorSlot = i;
                    ma.armorId = this.itemsEquipped[i];
                    ma.armorDmg = this.player.inventory.armorInventory[slotIndex].getMetadata();

                    this.player.inventory.armorInventory[slotIndex].writeToNBT(ma.itemData);
                    this.eventList.add(ma);
                }
            }
            else if (this.itemsEquipped[i] != -1)
            {
                this.itemsEquipped[i] = -1;
                Action ma = new Action(Action.EQUIP);
                ma.armorSlot = i;
                ma.armorId = this.itemsEquipped[i];
                ma.armorDmg = 0;
                this.eventList.add(ma);
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
        ItemStack mainhand = this.player.getHeldItemMainhand();
        ItemStack offhand = this.player.getHeldItemOffhand();

        boolean blank = this.trackItemToSlot(mainhand, 0) || this.trackItemToSlot(offhand, 5);
    }

    private boolean trackItemToSlot(ItemStack item, int slot)
    {
        if (item != null)
        {
            int id = Item.getIdFromItem(item.getItem());

            if (id != this.itemsEquipped[slot])
            {
                this.itemsEquipped[slot] = id;

                Action ma = new Action(Action.EQUIP);
                ma.armorSlot = slot;
                ma.armorId = this.itemsEquipped[slot];
                ma.armorDmg = item.getMetadata();

                item.writeToNBT(ma.itemData);
                this.eventList.add(ma);

                return true;
            }
        }
        else if (this.itemsEquipped[slot] != -1)
        {
            this.itemsEquipped[slot] = -1;

            Action ma = new Action(Action.EQUIP);
            ma.armorSlot = slot;
            ma.armorId = this.itemsEquipped[slot];
            ma.armorDmg = 0;

            this.eventList.add(ma);

            return true;
        }

        return false;
    }

    /**
     * Track the hand swing (like when you do the tap-tap with left-click)
     */
    private void trackSwing()
    {
        if (this.player.isSwingInProgress && !this.lastTickSwipe)
        {
            this.lastTickSwipe = true;
            this.eventList.add(new Action(Action.SWIPE));
        }
        else
        {
            this.lastTickSwipe = false;
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
        if (this.eventList.size() <= 0)
        {
            this.in.writeBoolean(false);
            return;
        }

        Action ma = this.eventList.get(0);

        this.in.writeBoolean(true);
        this.in.writeByte(ma.type);

        switch (ma.type)
        {
            case Action.CHAT:
                this.in.writeUTF(ma.message);
                break;

            case Action.DROP:
                CompressedStreamTools.write(ma.itemData, this.in);
                break;

            case Action.EQUIP:
                this.in.writeInt(ma.armorSlot);
                this.in.writeInt(ma.armorId);
                this.in.writeInt(ma.armorDmg);

                if (ma.armorId != -1)
                    CompressedStreamTools.write(ma.itemData, this.in);
                break;

            case Action.SHOOTARROW:
                this.in.writeInt(ma.arrowCharge);
                break;

            case Action.LOGOUT:
                Mocap.records.remove(this.player);
                Mocap.broadcastMessage("Stopped recording " + this.player.getDisplayName().getFormattedText() + ". Bye!");

                this.capture = false;
                break;

            case Action.PLACEBLOCK:
                this.in.writeInt(ma.xCoord);
                this.in.writeInt(ma.yCoord);
                this.in.writeInt(ma.zCoord);
                CompressedStreamTools.write(ma.itemData, this.in);
                break;

            case Action.MOUNTING:
                this.in.writeLong(ma.target.getMostSignificantBits());
                this.in.writeLong(ma.target.getLeastSignificantBits());
                this.in.writeInt(ma.armorSlot);
                break;
        }

        this.eventList.remove(0);
    }
}
