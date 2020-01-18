package mchorse.blockbuster.aperture.network.server;

import mchorse.blockbuster.aperture.network.common.PacketPlaybackButton;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class ServerHandlerPlaybackButton extends ServerMessageHandler<PacketPlaybackButton>
{
    @Override
    public void run(EntityPlayerMP player, PacketPlaybackButton message)
    {
        ItemStack stack = player.getHeldItemMainhand();
        NBTTagCompound compound = stack.getTagCompound();

        if (stack == null || !(stack.getItem() instanceof ItemPlayback) || compound == null)
        {
            return;
        }

        compound.removeTag("CameraPlay");
        compound.removeTag("CameraProfile");
        compound.removeTag("Scene");

        if (message.scene != null)
        {
            compound.removeTag("DirX");
            compound.removeTag("DirY");
            compound.removeTag("DirZ");
            compound.setString("Scene", message.scene);
        }
        else if (message.director != null)
        {
            NBTUtils.saveBlockPos("Dir", compound, message.director);
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
