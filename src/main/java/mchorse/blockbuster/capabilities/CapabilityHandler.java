package mchorse.blockbuster.capabilities;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.MorphingProvider;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.network.common.PacketMorphPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * Capability handler class
 *
 * This class is responsible for managing capabilities, i.e. attaching
 * capabilities and syncing values on the client.
 */
public class CapabilityHandler
{
    public static final ResourceLocation MORPHING_CAP = new ResourceLocation(Blockbuster.MODID, "morphing_capability");

    /**
     * Attach capabilities (well, only one, right now)
     */
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.Entity event)
    {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        event.addCapability(MORPHING_CAP, new MorphingProvider());
    }

    @SubscribeEvent
    public void playerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability != null)
        {
            Dispatcher.sendTo(new PacketMorph(capability.getModel(), capability.getSkin()), (EntityPlayerMP) player);
        }
    }

    /**
     * When player starts tracking another player, server has to send the
     * morphing values
     */
    @SubscribeEvent
    public void playerStartsTracking(StartTracking event)
    {
        if (event.getTarget() instanceof EntityPlayer)
        {
            Entity target = event.getTarget();
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            IMorphing cap = target.getCapability(MorphingProvider.MORPHING_CAP, null);

            Dispatcher.sendTo(new PacketMorphPlayer(target.getEntityId(), cap.getModel(), cap.getSkin()), player);
        }
    }
}
