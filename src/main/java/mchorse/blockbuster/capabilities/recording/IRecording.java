package mchorse.blockbuster.capabilities.recording;

/**
 * Recording capability
 *
 * This capability is responsible for tracking client player's resources such
 * as loaded records and camera profile (and also some data related to tracking
 * the changes of these resources).
 *
 * I think it will be server-side only capability (no need to sync with client).
 */
public interface IRecording
{
    /**
     * Does player has loaded recording?
     */
    public boolean hasRecording(String filename);

    /**
     * What is the last time given recording was updated?
     */
    public long recordingTimestamp(String filename);

    /**
     * Add a recording
     */
    public void addRecording(String filename, long timestamp);

    /**
     * Remove a recording
     */
    public void removeRecording(String filename);

    /**
     * Remove all recordings
     */
    public void removeRecordings();

    /**
     * Update given recording's timestamp
     */
    public void updateRecordingTimestamp(String filename, long timestamp);
}