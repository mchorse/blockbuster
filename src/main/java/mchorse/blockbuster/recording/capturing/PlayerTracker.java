package mchorse.blockbuster.recording.capturing;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.recording.RecordRecorder;
import mchorse.blockbuster.recording.actions.AttackAction;
import mchorse.blockbuster.recording.actions.EquipAction;
import mchorse.blockbuster.recording.actions.SwipeAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Player tracker class
 *
 * This class tracks player's properties such as arm swing and player equipped
 * inventory. That's it.
 */
public class PlayerTracker
{
    /**
     * Record recorder to which tracked stuff are going to be added
     */
    public RecordRecorder recorder;

    /* Items to track */
    private ItemStack[] items = new ItemStack[6];

    public PlayerTracker(RecordRecorder recorder)
    {
        this.recorder = recorder;
    }

    /**
     * Track player's properties like armor, hand held items and hand swing
     */
    public void track(EntityPlayer player)
    {
        this.trackSwing(player);
        this.trackHeldItem(player);
        this.trackArmor(player);
    }

    /**
     * Track armor inventory
     */
    private void trackArmor(EntityPlayer player)
    {
        for (int i = 1; i < 5; i++)
        {
            this.trackItemToSlot(player.inventory.armorInventory.get(i - 1), i);
        }
    }

    /**
     * Track held items
     */
    private void trackHeldItem(EntityPlayer player)
    {
        ItemStack mainhand = player.getHeldItemMainhand();
        ItemStack offhand = player.getHeldItemOffhand();

        this.trackItemToSlot(mainhand, 0);
        this.trackItemToSlot(offhand, 5);
    }

    /**
     * Track item to slot.
     *
     * This is a simple utility method that reduces number of lines for both
     * hands.
     */
    private boolean trackItemToSlot(ItemStack item, int slot)
    {
        if (!item.isEmpty())
        {
            if (item != this.items[slot])
            {
                this.items[slot] = item;
                this.recorder.actions.add(new EquipAction((byte) slot, item));

                return true;
            }
        }
        else if (this.items[slot] != null)
        {
            this.items[slot] = null;
            this.recorder.actions.add(new EquipAction((byte) slot, null));

            return true;
        }

        return false;
    }

    /**
     * Track the hand swing (like when you do the tap-tap with left-click)
     */
    private void trackSwing(EntityPlayer player)
    {
        /**
         * player.isPlayerSleeping() is necessary since for some reason when 
         * player is falling asleep, the isSwingInProgress is true, while 
         * player.swingProgress is equals to 0 which makes player swipe every 
         * tick in bed.
         * 
         * So gotta check that so it wouldn't look like Steve is beating his 
         * meat.
         */
        if (player.isSwingInProgress && player.swingProgress == 0 && !player.isPlayerSleeping())
        {
            this.recorder.actions.add(new SwipeAction());

            if (Blockbuster.recordAttackOnSwipe.get())
            {
                this.recorder.actions.add(new AttackAction());
            }
        }
    }
}