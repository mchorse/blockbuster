package noname.blockbuster.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import noname.blockbuster.entity.ActorEntity;

public class ActorRegistry
{
    protected static Map<ActorEntity, EntityPlayer> recordingActors = new HashMap<ActorEntity, EntityPlayer>();

    /* (Un)registering actor records */

    public static void registerRecording(EntityPlayer master, ActorEntity puppet)
    {
        recordingActors.put(puppet, master);
    }

    public static void unregisterRecording(EntityPlayer master)
    {
        recordingActors.remove(master);
    }

    public static boolean isActorRecording(ActorEntity puppet)
    {
        return recordingActors.containsKey(puppet);
    }
}
