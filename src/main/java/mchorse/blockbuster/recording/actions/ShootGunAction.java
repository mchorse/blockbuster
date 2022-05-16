package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.server.gun.ServerHandlerGunInteract;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ShootGunAction extends Action
{
    private ItemStack stack;

    public ShootGunAction()
    {
        this(ItemStack.EMPTY);
    }

    public ShootGunAction(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
        EntityPlayer player = actor instanceof EntityPlayer ? (EntityPlayer) actor : ((EntityActor) actor).fakePlayer;

        if (frame == null)
        {
            return;
        }

        if (player != null)
        {
            GunProps props = NBTUtils.getGunProps(this.stack);

            if (props != null)
            {
                player.width = actor.width;
                player.height = actor.height;
                player.eyeHeight = actor.getEyeHeight();
                player.setEntityBoundingBox(actor.getEntityBoundingBox());

                player.posX = actor.posX;
                player.posY = actor.posY;
                player.posZ = actor.posZ;
                player.rotationYaw = frame.yaw;
                player.rotationPitch = frame.pitch;
                player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, actor.getHeldItemMainhand());
                player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, actor.getHeldItemOffhand());

                ServerHandlerGunInteract.interactWithGun(null, actor, this.stack);
            }
            else
            {
                Blockbuster.LOGGER.error("Null gun props");
            }
        }

    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);

        this.stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);

        ByteBufUtils.writeItemStack(buf, this.stack);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.stack = new ItemStack(tag.getCompoundTag("Stack"));
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setTag("Stack", this.stack.serializeNBT());
    }
}