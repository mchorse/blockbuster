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
     * Get filename of current camera profile
     */
    public String currentProfile();

    /**
     * Get timestamp of current camera profile
     */
    public long currentProfileTimestamp();

    /**
     * Does this capability has a camera profile?
     */
    public boolean hasProfile();

    /**
     * Set current's camera profile filename
     */
    public void setCurrentProfile(String filename);

    /**
     * Set current's camera profile timestamp
     */
    public void setCurrentProfileTimestamp(long timestamp);

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
     * Update given recording's timestamp
     */
    public void updateRecordingTimestamp(String filename, long timestamp);
}