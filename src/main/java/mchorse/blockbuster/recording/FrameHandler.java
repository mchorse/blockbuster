package mchorse.blockbuster.recording;

import mchorse.blockbuster.common.ClientProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Frame handler
 *
 * This class is responsible for recording frames on the client side.
 */
@SideOnly(Side.CLIENT)
public class FrameHandler
{
    /**
     * This is going to record the player actions
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        EntityPlayer player = event.player;

        if (event.phase == Phase.START)
        {
            return;
        }

        if (player.worldObj.isRemote && ClientProxy.manager.recorders.containsKey(player))
        {
            ClientProxy.manager.recorders.get(player).record(player);
        }
    }
}