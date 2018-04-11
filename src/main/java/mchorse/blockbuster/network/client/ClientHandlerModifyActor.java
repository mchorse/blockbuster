package mchorse.blockbuster.network.client;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.PacketModifyActor;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerModifyActor extends ClientMessageHandler<PacketModifyActor>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketModifyActor message)
    {
        EntityActor actor = (EntityActor) player.world.getEntityByID(message.id);

        actor.modify(message.morph, message.invisible, false);
    }
}