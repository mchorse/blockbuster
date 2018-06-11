package mchorse.blockbuster.recording.actions;

import java.util.Random;

import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

/**
 * Item drop action
 *
 * Actor tosses held item away just like player when pressing "q" key
 */
public class DropAction extends Action
{
    public NBTTagCompound itemData;

    public DropAction()
    {
        this.itemData = new NBTTagCompound();
    }

    public DropAction(ItemStack item)
    {
        this();

        item.writeToNBT(this.itemData);
    }

    @Override
    public byte getType()
    {
        return Action.DROP;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        final float PI = 3.1415927F;

        Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
        ItemStack items = ItemStack.loadItemStackFromNBT(this.itemData);

        EntityItem item = new EntityItem(actor.worldObj, actor.posX, actor.posY - 0.3D + actor.getEyeHeight(), actor.posZ, items);
        Random rand = new Random();

        float f = 0.3F;
        float yaw = frame.yaw;
        float pitch = frame.pitch;

        item.motionX = (-MathHelper.sin(yaw / 180.0F * PI) * MathHelper.cos(pitch / 180.0F * PI) * f);
        item.motionZ = (MathHelper.cos(yaw / 180.0F * PI) * MathHelper.cos(pitch / 180.0F * PI) * f);
        item.motionY = (-MathHelper.sin(pitch / 180.0F * PI) * f + 0.1F);
        item.setPickupDelay(40);

        f = 0.02F;
        float f1 = rand.nextFloat() * PI * 2.0F * rand.nextFloat();

        item.motionX += Math.cos(f1) * f;
        item.motionY += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
        item.motionZ += Math.sin(f1) * f;

        actor.worldObj.spawnEntityInWorld(item);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.itemData = tag.getCompoundTag("Data");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setTag("Data", this.itemData);
    }
}