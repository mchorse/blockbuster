package mchorse.blockbuster.network.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.PacketModifyActor;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerModifyActor extends ClientMessageHandler<PacketModifyActor>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketModifyActor message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        actor.modify(message.model, message.skin, message.invisible, false);
    }
}