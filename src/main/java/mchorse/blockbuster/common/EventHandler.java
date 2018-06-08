package mchorse.blockbuster.common;

import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketConfirmBreak;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Blockbuster's event handler
 *
 * This event handler is responsible for canceling out breaking of director
 * block. This is made for avoiding accidental removal of director block.
 */
public class EventHandler
{
    /**
     * On break block event, we are checking whether player is breaking director
     * block and it's not empty (has replays at least one replay). Thanks to
     * Tom Soel for suggesting this.
     */
    @SubscribeEvent
    public void onBreakBlock(BreakEvent event)
    {
        World world = event.getWorld();

        if (!world.isRemote && event.getState().getBlock() instanceof BlockDirector)
        {
            TileEntity tile = world.getTileEntity(event.getPos());

            if (tile instanceof TileEntityDirector)
            {
                TileEntityDirector director = (TileEntityDirector) tile;

                if (!director.replays.isEmpty())
                {
                    event.setCanceled(true);

                    Dispatcher.sendTo(new PacketConfirmBreak(event.getPos(), director.replays.size()), (EntityPlayerMP) event.getPlayer());
                }
            }
        }
    }
}