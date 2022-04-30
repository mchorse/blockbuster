package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer.GunEntry;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerGunInfo extends ClientMessageHandler<PacketGunInfo>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketGunInfo message)
    {
        Entity entity = player.world.getEntityByID(message.entity);

        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase base = (EntityLivingBase) entity;
            ItemStack stack = base.getHeldItemMainhand();
            
            if (!stack.isEmpty())
            {
                NBTUtils.saveGunProps(stack, message.tag);

                if (TileEntityGunItemStackRenderer.models.containsKey(stack))
                {
                    GunEntry entry = TileEntityGunItemStackRenderer.models.get(stack);
                    GunProps props = new GunProps(message.tag);

                    entry.props.state = props.state;
                }
            }
        }
    }
}