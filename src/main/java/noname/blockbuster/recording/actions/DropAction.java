package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import noname.blockbuster.entity.EntityActor;

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
    public void apply(EntityActor actor)
    {
        final float PI = 3.1415927F;

        ItemStack items = ItemStack.loadItemStackFromNBT(this.itemData);

        EntityItem item = new EntityItem(actor.worldObj, actor.posX, actor.posY - 0.3D + actor.getEyeHeight(), actor.posZ, items);
        Random rand = new Random();

        float f = 0.3F;

        item.motionX = (-MathHelper.sin(actor.rotationYaw / 180.0F * PI) * MathHelper.cos(actor.rotationPitch / 180.0F * PI) * f);
        item.motionZ = (MathHelper.cos(actor.rotationYaw / 180.0F * PI) * MathHelper.cos(actor.rotationPitch / 180.0F * PI) * f);
        item.motionY = (-MathHelper.sin(actor.rotationPitch / 180.0F * PI) * f + 0.1F);
        item.setDefaultPickupDelay();

        f = 0.02F;
        float f1 = rand.nextFloat() * PI * 2.0F * rand.nextFloat();

        item.motionX += Math.cos(f1) * f;
        item.motionY += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
        item.motionZ += Math.sin(f1) * f;

        actor.worldObj.spawnEntityInWorld(item);
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.itemData = CompressedStreamTools.read((DataInputStream) in);
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        CompressedStreamTools.write(this.itemData, out);
    }
}
