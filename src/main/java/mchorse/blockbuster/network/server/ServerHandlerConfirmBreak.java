package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.network.common.PacketConfirmBreak;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class ServerHandlerConfirmBreak extends ServerMessageHandler<PacketConfirmBreak>
{
    @Override
    public void run(EntityPlayerMP player, PacketConfirmBreak message)
    {
        World world = player.worldObj;

        if (world.getBlockState(message.pos).getBlock() instanceof BlockDirector)
        {
            world.destroyBlock(message.pos, !player.isCreative());
        }
    }
}