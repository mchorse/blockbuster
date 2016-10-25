package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerPlayback extends ClientMessageHandler<PacketPlayback>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketPlayback message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        if (message.state)
        {
            if (ClientProxy.manager.records.containsKey(message.filename))
            {
                Record record = ClientProxy.manager.records.get(message.filename);
                RecordPlayer recordPlayer = new RecordPlayer(record, Mode.FRAMES);

                actor.playback = recordPlayer;

                /* TODO: sync tick and delay */
            }
            else
            {
                /* TODO: request frames from the server */
            }
        }
        else
        {
            actor.playback = null;
        }
    }
}
