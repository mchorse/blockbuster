package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ServerHandlerPlaybackButton extends ServerMessageHandler<PacketPlaybackButton>
{
    @Override
    public void run(EntityPlayerMP player, PacketPlaybackButton message)
    {
        ItemStack stack = player.getHeldItemMainhand();
        NBTTagCompound compound = stack.getTagCompound();

        if (compound == null)
        {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }

        if (!(stack.getItem() instanceof ItemPlayback))
        {
            return;
        }

        compound.removeTag("CameraPlay");
        compound.removeTag("CameraProfile");
        compound.removeTag("Scene");

        compound.removeTag("DirX");
        compound.removeTag("DirY");
        compound.removeTag("DirZ");

        if (message.location.isScene())
        {
            compound.setString("Scene", message.location.getFilename());
        }

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
