package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.common.PacketModifyActor;

@SideOnly(Side.CLIENT)
public class ClientHandlerModifyActor extends ClientMessageHandler<PacketModifyActor>
{
    @Override
    public void run(EntityPlayerSP player, PacketModifyActor message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        actor.modify(message.filename, message.name, message.skin, message.invulnerable, false);
    }
}
