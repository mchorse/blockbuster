package mchorse.blockbuster.network.server;

import java.lang.reflect.Field;
import java.util.Map;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketStructure;
import mchorse.blockbuster.network.common.PacketStructureRequest;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class ServerHandlerStructureRequest extends ServerMessageHandler<PacketStructureRequest>
{
    @Override
    @SuppressWarnings("unchecked")
    public void run(EntityPlayerMP player, PacketStructureRequest message)
    {
        WorldServer world = player.getServerWorld();
        TemplateManager manager = world.getStructureTemplateManager();

        Field field = manager.getClass().getDeclaredFields()[0];

        field.setAccessible(true);

        try
        {
            Map<String, Template> templates = (Map<String, Template>) field.get(manager);

            for (Map.Entry<String, Template> entry : templates.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                entry.getValue().writeToNBT(tag);

                Dispatcher.sendTo(new PacketStructure(entry.getKey(), tag), player);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}