package mchorse.blockbuster_pack.trackers;

import java.util.LinkedHashMap;
import java.util.Map;

import mchorse.blockbuster_pack.client.gui.trackers.GuiBaseTracker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Tracker Registry
 */
public class TrackerRegistry
{
    public static final Map<String, Class<? extends BaseTracker>> ID_TO_CLASS = new LinkedHashMap<String, Class<? extends BaseTracker>>();

    public static final Map<Class<? extends BaseTracker>, String> CLASS_TO_ID = new LinkedHashMap<Class<? extends BaseTracker>, String>();

    @SideOnly(Side.CLIENT)
    public static Map<Class<? extends BaseTracker>, GuiBaseTracker<? extends BaseTracker>> CLIENT;

    public static void registerTracker(String id, Class<? extends BaseTracker> clazz)
    {
        ID_TO_CLASS.put(id, clazz);
        CLASS_TO_ID.put(clazz, id);
    }
}
