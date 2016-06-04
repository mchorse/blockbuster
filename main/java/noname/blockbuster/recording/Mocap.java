package noname.blockbuster.recording;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Mocap
{
    public static Map<EntityPlayer, Recorder> recordThreads = Collections.synchronizedMap(new HashMap());
    public static final short signature = 3208;
    public static final long delay = 100L;

    public static List<Action> getActionListForPlayer(EntityPlayer ep)
    {
        Recorder aRecorder = recordThreads.get(ep);

        if (aRecorder == null)
        {
            return null;
        }

        return aRecorder.eventsList;
    }

    public static void broadcastMessage(String message)
    {
        ITextComponent chatMessage = new TextComponentString(message);
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getAllUsernames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                player.addChatMessage(chatMessage);
            }
        }
    }

    public static EntityEquipmentSlot getSlotByIndex(int index)
    {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
        {
            if (slot.func_188452_c() == index)
                return slot;
        }

        return null;
    }

    public static String replayFile(String filename)
    {
        /* You spin me round round, baby round round */
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/records");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return file.getAbsolutePath() + "/" + filename;
    }

    public static Entity entityByUUID(World world, UUID target)
    {
        for (Entity entity : world.getLoadedEntityList())
        {
            if (entity.getUniqueID().equals(target))
            {
                return entity;
            }
        }

        return null;
    }
}