package noname.blockbuster.recording;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recorder
{
    public RecordThread thread;
    public List<Action> eventsList = Collections.synchronizedList(new ArrayList<Action>());
    public String fileName;
}