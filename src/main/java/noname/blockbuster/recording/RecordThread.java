package noname.blockbuster.recording;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import noname.blockbuster.recording.actions.Action;
import noname.blockbuster.recording.actions.EquipAction;
import noname.blockbuster.recording.actions.SwipeAction;

/**
 * Record thread
 *
 * This class is responsible for recording all player's actions into a given file.
 * That's includes: movement, rotation, and the actions that player is commiting
 * during recording.
 */
class RecordThread implements Runnable
{
    public Thread thread;
    public boolean capture = false;

    private EntityPlayer player;
    private RandomAccessFile in;
    private boolean lastTickSwipe = false;
    private int[] itemsEquipped = new int[6];
    private List<Action> eventList;

    RecordThread(EntityPlayer player, List<Action> events, String filename)
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

        this.player = player;
        this.capture = true;
        this.eventList = events;

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
                    Mocap.broadcastMessage(I18n.format("blockbuster.mocap.stopped_dead", this.player.getDisplayName().getFormattedText()));
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
        Entity entity = this.player.isRiding() ? this.player.getRidingEntity() : this.player;

        this.in.writeFloat(entity.rotationYaw);
        this.in.writeFloat(entity.rotationPitch);
        this.in.writeDouble(entity.posX);
        this.in.writeDouble(entity.posY);
        this.in.writeDouble(entity.posZ);
        this.in.writeFloat(this.player.moveForward);
        this.in.writeFloat(this.player.moveStrafing);
        this.in.writeDouble(entity.motionX);
        this.in.writeDouble(entity.motionY);
        this.in.writeDouble(entity.motionZ);
        this.in.writeFloat(entity.fallDistance);
        this.in.writeBoolean(entity.isAirBorne);
        this.in.writeBoolean(entity.isSneaking());
        this.in.writeBoolean(entity.isSprinting());
        this.in.writeBoolean(entity.onGround);
    }

    /**
     * Track armor inventory
     */
    private void trackArmor()
    {
        for (int i = 1; i < 5; i++)
        {
            this.trackItemToSlot(this.player.inventory.armorInventory[i - 1], i);
        }
    }

    /**
     * Track held items
     */
    private void trackHeldItem()
    {
        ItemStack mainhand = this.player.getHeldItemMainhand();
        ItemStack offhand = this.player.getHeldItemOffhand();

        boolean blank = this.trackItemToSlot(mainhand, 0) || this.trackItemToSlot(offhand, 5);
    }

    /**
     * Track item to slot.
     *
     * This is a simple utility method that reduces number of lines for both
     * hands.
     */
    private boolean trackItemToSlot(ItemStack item, int slot)
    {
        if (item != null)
        {
            int id = Item.getIdFromItem(item.getItem());

            if (id != this.itemsEquipped[slot])
            {
                this.itemsEquipped[slot] = id;
                this.eventList.add(new EquipAction((byte) slot, (short) id, item));

                return true;
            }
        }
        else if (this.itemsEquipped[slot] != -1)
        {
            this.itemsEquipped[slot] = -1;
            this.eventList.add(new EquipAction((byte) slot, (short) -1, null));

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
            this.eventList.add(new SwipeAction());
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
        if (this.eventList.size() == 0)
        {
            this.in.writeBoolean(false);
            return;
        }

        Action action = this.eventList.remove(0);

        this.in.writeBoolean(true);
        this.in.writeByte(action.type);
        action.toBytes(this.in);

        if (action.type == Action.LOGOUT)
        {
            this.capture = false;
            Mocap.records.remove(this.player);
            Mocap.broadcastMessage(I18n.format("blockbuster.mocap.stopped_logout", this.player.getDisplayName().getUnformattedText()));
        }
    }
}
