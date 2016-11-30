package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ServerHandlerPlaybackButton extends ServerMessageHandler<PacketPlaybackButton>
{
    @Override
    public void run(EntityPlayerMP player, PacketPlaybackButton message)
    {
        ItemStack stack = player.getHeldItem();
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
