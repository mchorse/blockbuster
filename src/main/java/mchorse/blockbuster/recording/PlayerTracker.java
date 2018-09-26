package mchorse.blockbuster.recording;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.recording.actions.AttackAction;
import mchorse.blockbuster.recording.actions.EquipAction;
import mchorse.blockbuster.recording.actions.SwipeAction;
import net.minecraft.entity.EntityLivingBase;
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

    /**
     * Track player swing progress, apparently, during player sleep, 
     * {@link EntityLivingBase#isSwingInProgress} is true, however, its value 
     * doesn't change over time. So gotta track that so it wouldn't look like 
     * Steve is beating his meat.
     */
    private float lastSwing;

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
            this.trackItemToSlot(player.inventory.armorInventory[i - 1], i);
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
        if (item != null)
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
        if (player.isSwingInProgress && player.swingProgress == 0 && player.swingProgress != this.lastSwing)
        {
            this.recorder.actions.add(new SwipeAction());

            if (Blockbuster.proxy.config.record_attack_on_swipe)
            {
                this.recorder.actions.add(new AttackAction());
            }
        }

        this.lastSwing = player.swingProgress;
    }
}