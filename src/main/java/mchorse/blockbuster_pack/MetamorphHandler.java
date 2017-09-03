package mchorse.blockbuster_pack;

import mchorse.metamorph.api.events.RegisterBlacklistEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MetamorphHandler
{
    @SubscribeEvent
    public void onBlacklistReload(RegisterBlacklistEvent event)
    {
        event.blacklist.add("blockbuster.Actor");
    }
}