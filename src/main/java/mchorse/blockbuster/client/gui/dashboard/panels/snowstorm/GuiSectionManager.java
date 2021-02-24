package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm;

import java.util.HashMap;
import java.util.Map;

public class GuiSectionManager
{
    private static final Map<String, Boolean> STATES = new HashMap<String, Boolean>();

    public static boolean isCollapsed(String id)
    {
        Boolean state = STATES.get(id);
        
        if (state == null)
        {
            state = true; // default value
            STATES.put(id, state);
        }
        
        return state;
    }

    public static void setCollapsed(String id, boolean collapsed)
    {
        STATES.put(id, collapsed);
    }
    
    /**
     * This method only adds a state to the Map if the id isn't present
     * @param id
     * @param collapsed
     */
    public static void setDefaultState(String id, boolean collapsed)
    {
        if (!STATES.containsKey(id))
        {
            STATES.put(id, collapsed);
        }
    }
}
