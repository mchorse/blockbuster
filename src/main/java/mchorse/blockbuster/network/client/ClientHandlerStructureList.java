package mchorse.blockbuster.network.client;

import mchorse.blockbuster.network.common.structure.PacketStructureList;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.blockbuster_pack.morphs.StructureMorph.StructureRenderer;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerStructureList extends ClientMessageHandler<PacketStructureList>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketStructureList message)
    {
        for (String str : message.structures)
        {
            StructureRenderer renderer = StructureMorph.STRUCTURES.get(str);

            if (renderer == null)
            {
                renderer = new StructureRenderer();
                StructureMorph.STRUCTURES.put(str, renderer);
            }
        }
    }
}