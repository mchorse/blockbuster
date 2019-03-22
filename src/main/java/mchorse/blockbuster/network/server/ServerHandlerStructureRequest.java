package mchorse.blockbuster.network.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructure;
import mchorse.blockbuster.network.common.structure.PacketStructureRequest;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.common.DimensionManager;

public class ServerHandlerStructureRequest extends ServerMessageHandler<PacketStructureRequest>
{
    public static List<String> getAllStructures()
    {
        List<String> structures = new ArrayList<String>();
        File files = new File(DimensionManager.getCurrentSaveRootDirectory(), "structures");

        if (!files.isDirectory())
        {
            return structures;
        }

        for (File file : files.listFiles())
        {
            String name = file.getName();

            if (file.isFile() && name.endsWith(".nbt"))
            {
                structures.add(name.substring(0, name.lastIndexOf(".")));
            }
        }

        return structures;
    }

    @Override
    public void run(EntityPlayerMP player, PacketStructureRequest message)
    {
        WorldServer world = player.getServerWorld();
        TemplateManager manager = world.getStructureTemplateManager();

        try
        {
            if (!message.name.isEmpty())
            {
                this.sendTemplate(player, message.name, manager.getTemplate(player.mcServer, new ResourceLocation(message.name)));

                return;
            }

            for (String struct : getAllStructures())
            {
                this.sendTemplate(player, struct, manager.getTemplate(player.mcServer, new ResourceLocation(struct)));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Send the template to a player 
     */
    public void sendTemplate(EntityPlayerMP player, String key, Template template)
    {
        NBTTagCompound tag = new NBTTagCompound();

        template.writeToNBT(tag);
        Dispatcher.sendTo(new PacketStructure(key, tag), player);
    }
}