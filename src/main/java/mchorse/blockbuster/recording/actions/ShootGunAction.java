package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.network.common.guns.PacketGunProjectile;
import mchorse.blockbuster.network.common.guns.PacketGunShot;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.lwjgl.BufferUtils;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ShootGunAction extends Action{


    @Override
    public void apply(EntityLivingBase actor)
    {

        Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
        EntityPlayer player = actor instanceof EntityPlayer ? (EntityPlayer) actor : ((EntityActor) actor).fakePlayer;
        if (frame == null) return;
        if (player != actor) {
            Dispatcher.sendToServer(new PacketGunInteract(player.getHeldItem(EnumHand.MAIN_HAND)));
        }

    }
    @Override
    public boolean isSafe()
    {
        return true;
    }
    @Override
    public void fromBuf(ByteBuf buf) {
        super.fromBuf(buf);

    }

    @Override
    public void toBuf(ByteBuf buf) {
        super.toBuf(buf);
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        super.fromNBT(tag);
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
        super.toNBT(tag);
    }
}
