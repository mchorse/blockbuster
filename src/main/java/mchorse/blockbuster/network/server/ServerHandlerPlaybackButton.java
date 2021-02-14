package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ServerHandlerPlaybackButton extends ServerMessageHandler<PacketPlaybackButton>
{
    @Override
    public void run(EntityPlayerMP player, PacketPlaybackButton message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        ItemStack stack = player.getHeldItemMainhand();

        if (!(stack.getItem() instanceof ItemPlayback))
        {
            stack = player.getHeldItemOffhand();
        }

        if (!(stack.getItem() instanceof ItemPlayback))
        {
            return;
        }

        NBTTagCompound compound = stack.getTagCompound();

        if (compound == null)
        {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }

        compound.removeTag("CameraPlay");
        compound.removeTag("CameraProfile");
        compound.removeTag("Scene");

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
