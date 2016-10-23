package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerFrames extends ClientMessageHandler<PacketFramesLoad>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketFramesLoad message)
    {
        System.out.println("Hello?");

        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);
        Record record = new Record(message.filename);
        record.frames = message.frames;

        Dispatcher.sendToServer(new PacketPlayback(message.id, true));

        actor.playback = new RecordPlayer(record, RecordPlayer.Mode.FRAMES);
        actor.playback.playing = true;
    }
}
