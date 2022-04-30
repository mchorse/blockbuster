package mchorse.blockbuster.network.client;

import mchorse.blockbuster.network.common.PacketActorPause;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerActorPause extends ClientMessageHandler<PacketActorPause>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketActorPause message)
    {
        EntityLivingBase actor = (EntityLivingBase) player.world.getEntityByID(message.id);
        RecordPlayer playback = EntityUtils.getRecordPlayer(actor);

        if (playback != null)
        {
            if (message.pause) playback.pause();
            else playback.resume(message.tick);

            playback.tick = message.tick;
            playback.playing = !message.pause;

            if (playback.record != null)
            {
                Record record = playback.record;

                playback.applyFrame(message.tick - 1, actor, true);

                Frame frame = record.getFrameSafe(message.tick - record.preDelay - 1);

                if (frame != null && frame.hasBodyYaw)
                {
                    actor.renderYawOffset = frame.bodyYaw;
                }

                actor.lastTickPosX = actor.prevPosX = actor.posX;
                actor.lastTickPosY = actor.prevPosY = actor.posY;
                actor.lastTickPosZ = actor.prevPosZ = actor.posZ;
                actor.prevRotationPitch = actor.rotationPitch;
                actor.prevRotationYaw = actor.rotationYaw;
                actor.prevRotationYawHead = actor.rotationYawHead;
                actor.prevRenderYawOffset = actor.renderYawOffset;
            }
        }
    }
}