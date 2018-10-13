package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.recording.PacketUpdatePlayerData;
import mchorse.blockbuster.recording.MPMHelper;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class ServerHandlerUpdatePlayerData extends ServerMessageHandler<PacketUpdatePlayerData>
{
    @Override
    public void run(EntityPlayerMP player, PacketUpdatePlayerData message)
    {
        Record record = null;

        try
        {
            record = CommonProxy.manager.getRecord(message.record);
        }
        catch (Exception e)
        {}

        if (record == null)
        {
            L10n.error(player, "record.not_exist", message.record);

            return;
        }

        NBTTagCompound tag = new NBTTagCompound();

        player.writeEntityToNBT(tag);
        record.playerData = tag;

        if (MPMHelper.isLoaded())
        {
            tag = MPMHelper.getMPMData(player);

            if (tag != null)
            {
                record.playerData.setTag("MPMData", tag);
            }
        }

        record.dirty = true;
        record.resetUnload();
    }
}