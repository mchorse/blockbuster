package mchorse.blockbuster.actor;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class CapabilityHandler
{
    public static final ResourceLocation MORPHING_CAP = new ResourceLocation(Blockbuster.MODID, "morphing_capability");

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
}
