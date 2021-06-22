package mchorse.blockbuster.capabilities.recording;

import mchorse.blockbuster.recording.RecordPlayer;
import net.minecraft.util.math.BlockPos;

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
     * Get last edited scene
     */
    public String getLastScene();

    /**
     * Set last edited scene
     */
    public void setLastScene(String scene);

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

    /**
     * Set last teleported block position  
     */
    public void setLastTeleportedBlockPos(BlockPos pos);

    /**
     * Get last teleported block position 
     */
    public BlockPos getLastTeleportedBlockPos();

    /**
     * Set record player which will animate this player 
     */
    public void setRecordPlayer(RecordPlayer player);

    /**
     * Get the record player which animates this player 
     */
    public RecordPlayer getRecordPlayer();

    /**
     * Whether this player is fake
     */
    public boolean isFakePlayer();

    /**
     * Set fake player
     */
    public void setFakePlayer(boolean fakePlayer);
}