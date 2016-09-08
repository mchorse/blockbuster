package mchorse.blockbuster.network.server;

import mchorse.blockbuster.item.ItemPlayback;
import mchorse.blockbuster.network.common.PacketPlayback;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ServerHandlerPlaybackButton extends ServerMessageHandler<PacketPlayback>
{
    @Override
    public void run(EntityPlayerMP player, PacketPlayback message)
    {
        ItemStack stack = player.getHeldItemMainhand();
        NBTTagCompound compound = stack.getTagCompound();

        if (stack == null || !(stack.getItem() instanceof ItemPlayback) || compound == null)
        {
            return;
        }

        compound.removeTag("CameraPlay");
        compound.removeTag("CameraProfile");

        if (message.mode == 0) return;

        if (message.mode == 1)
        {
            compound.setBoolean("CameraPlay", true);
        }
        else if (message.mode == 2)
        {
            compound.setString("CameraProfile", message.profile);
        }
    }
}
