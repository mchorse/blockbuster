package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ShootGunAction extends Action{
    public ShootGunAction() {
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
        EntityPlayer player = actor instanceof EntityPlayer ? (EntityPlayer) actor : ((EntityActor) actor).fakePlayer;
        if (frame == null) return;
        if (player!=null){
            ItemGun gun = (ItemGun) player.getHeldItemMainhand().getItem();
            GunProps props = NBTUtils.getGunProps(player.getHeldItemMainhand());
            if (props!=null){
            gun.shootIt(player.getHeldItemMainhand(), player,player.world);
            //Client Staff
                Dispatcher.sendTo(new PacketGunInteract(player.getHeldItemMainhand(),player.getEntityId()), (EntityPlayerMP) player);
            }else {Blockbuster.LOGGER.error("Null gun props");}
        }

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
