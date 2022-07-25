package mchorse.blockbuster.aperture.network.server;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.aperture.network.common.PacketAudioShift;
import mchorse.blockbuster.audio.AudioState;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerAudioShift extends ServerMessageHandler<PacketAudioShift>
{
    @Override
    public void run(EntityPlayerMP player, PacketAudioShift message)
    {
        Scene scene = message.get(player.world);

        if (scene != null)
        {
            scene.setAudioShift(message.shift);
            scene.sendAudio(AudioState.SET, scene.getTick());

            try
            {
                CommonProxy.scenes.save(scene.getId(), scene, false);
            }
            catch (Exception e)
            {}
        }
    }
}
