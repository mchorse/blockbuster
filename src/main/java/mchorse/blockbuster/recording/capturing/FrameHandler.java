package mchorse.blockbuster.recording.capturing;

import java.util.Objects;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketDamageControlCheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
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
    
    private BlockPos last = null;
    
    /**
     * This is going to record the player actions
     * and check DamageControl
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        EntityPlayer player = event.player;

        if (event.phase == Phase.START)
        {
            return;
        }

        if (player.world.isRemote)
        {
            if (ClientProxy.manager.recorders.containsKey(player))
            {
                ClientProxy.manager.recorders.get(player).record(player);
            }

            if (Blockbuster.damageControlMessage.get() && !CameraHandler.isCameraEditorOpen())
            {
                if (Minecraft.getMinecraft().objectMouseOver != null)
                {
                    BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
                    
                    if (pos != null && !Objects.equals(last, pos))
                    {
                        Dispatcher.sendToServer(new PacketDamageControlCheck(pos));
                    }
                    
                    last = pos;
                }
                else
                {
                    last = null;
                }
            }
        }
    }
}