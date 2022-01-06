package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunInfoStack;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ClientHandlerGunInfoStack extends ClientMessageHandler<PacketGunInfoStack>
{

    @Override
    @SideOnly(Side.CLIENT)
    public void run (EntityPlayerSP player, PacketGunInfoStack message) {
        ItemStack stack = message.stack;
        if (!stack.isEmpty())
        {
            NBTUtils.saveGunProps(stack, message.tag);
        }
    }
    
}