package mchorse.blockbuster.capabilities.recording;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Default implementation of {@link IRecording}
 */
public class Recording implements IRecording
{
    public Map<String, ItemInfo> recordings = new HashMap<String, ItemInfo>();

    public static IRecording get(EntityPlayer player)
    {
        return player.getCapability(RecordingProvider.RECORDING, null);
    }

    @Override
    public boolean hasRecording(String filename)
    {
        return this.recordings.containsKey(filename);
    }

    @Override
    public long recordingTimestamp(String filename)
    {
        return this.recordings.get(filename).timestamp;
    }

    @Override
    public void addRecording(String filename, long timestamp)
    {
        if (this.hasRecording(filename))
        {
            this.updateRecordingTimestamp(filename, timestamp);
        }
        else
        {
            this.recordings.put(filename, new ItemInfo(filename, timestamp));
        }
    }

    @Override
    public void removeRecording(String filename)
    {
        this.recordings.remove(filename);
    }

    @Override
    public void removeRecordings()
    {
        this.recordings.clear();
    }

    @Override
    public void updateRecordingTimestamp(String filename, long timestamp)
    {
        if (this.hasRecording(filename))
        {
            this.recordings.get(filename).timestamp = timestamp;
        }
    }

    /**
     * Item information class
     *
     * Instance of this class is responsible for storing information about a
     * file item like camera profile or recording with timestamp of when
     * it was changed.
     */
    public static class ItemInfo
    {
        public String filename;
        public long timestamp;

        public ItemInfo()
        {
            this("", -1);
        }

        public ItemInfo(String filename, long timestamp)
        {
            this.filename = filename;
            this.timestamp = timestamp;
        }
    }
}