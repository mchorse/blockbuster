package mchorse.blockbuster.recording;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.common.ClientProxy;
import net.minecraft.entity.player.EntityPlayer;

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