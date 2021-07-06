package mchorse.blockbuster.network.server;

import java.util.Map;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.common.PacketDamageControlCheck;
import mchorse.blockbuster.recording.capturing.DamageControl;
import mchorse.blockbuster.recording.capturing.DamageControl.BlockEntry;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class ServerHandlerDamageControlCheck extends ServerMessageHandler<PacketDamageControlCheck>
{
    @Override
    public void run(EntityPlayerMP player, PacketDamageControlCheck packet)
    {
        if (packet.pointPos != null && !player.world.isAirBlock(packet.pointPos))
        {
            Scene target = null;
            
            for (Map.Entry<Object, DamageControl> entry : CommonProxy.damage.damage.entrySet())
            {
                if (!(entry.getKey() instanceof Scene))
                {
                    continue;
                }
                
                DamageControl control = entry.getValue();
                
                for (BlockEntry block : control.blocks)
                {
                    if (block.pos.equals(packet.pointPos))
                    {
                        target = (Scene) entry.getKey();
                        break;
                    }
                }
                
                if (target != null)
                {
                    break;
                }
                
                double x = Math.abs(control.target.posX - (double) packet.pointPos.getX());
                double y = Math.abs(control.target.posY - (double) packet.pointPos.getY());
                double z = Math.abs(control.target.posZ - (double) packet.pointPos.getZ());
                
                if (x <= control.maxDistance && y <= control.maxDistance && z <= control.maxDistance)
                {
                    target = (Scene) entry.getKey();
                    break;
                }
            }
            
            if (target != null)
            {
                player.sendStatusMessage(new TextComponentTranslation(
                        "blockbuster.info.damage_control.message", target.getId()), true);
            }
        }
    }
}
