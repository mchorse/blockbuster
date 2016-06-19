package noname.blockbuster.recording;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import noname.blockbuster.recording.actions.Action;

/**
 * Container class for RecordThread, event list and information about
 * player's recording.
 */
public class Recorder
{
    public RecordThread thread;
    public List<Action> eventsList = Collections.synchronizedList(new ArrayList<Action>());
    public String fileName;
}