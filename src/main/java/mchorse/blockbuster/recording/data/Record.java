package mchorse.blockbuster.recording.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.actions.Action;

/**
 * This class stores actions and frames states for a recording (to be playbacked
 * or while recording).
 *
 * There's two list arrays in this class, index in both of these arrays
 * represents the frame position (0 is first frame). Frames list is always
 * populated, but actions list will contain some nulls.
 */
public class Record
{
    public static final short SIGNATURE = 131;

    public String filename = "";

    public List<Action> actions = new ArrayList<Action>();
    public List<Frame> frames = new ArrayList<Frame>();

    public Record(String filename)
    {
        this.filename = filename;
    }

    /**
     * Get the length of this record in frames/ticks
     */
    public int getLength()
    {
        return Math.max(this.actions.size(), this.frames.size());
    }

    /**
     * Apply a frame at given tick on the given actor. Don't pass tick value
     * less than 0, otherwise you might experience <s>tranquility</s> game
     * crash.
     */
    public void applyFrame(int tick, EntityActor actor)
    {
        if (tick > this.frames.size())
        {
            return;
        }

        this.frames.get(tick).applyOnActor(actor);
    }

    /**
     * Apply an action at the given tick on the given actor. Don't pass tick
     * value less than 0, otherwise you might experience game crash.
     */
    public void applyAction(int tick, EntityActor actor)
    {
        if (tick > this.actions.size())
        {
            return;
        }

        Action action = this.actions.get(tick);

        if (action != null)
        {
            action.apply(actor);
        }
    }

    /**
     * Save a recording to given file.
     *
     * This method basically writes the signature of the current version,
     * and then saves all available frames and actions.
     */
    public void toBytes(File file) throws IOException
    {
        RandomAccessFile buffer = new RandomAccessFile(file, "rw");
        int c = this.getLength();

        /* Version of the recording */
        buffer.writeShort(SIGNATURE);
        buffer.writeInt(c);

        for (int i = 0; i < c; i++)
        {
            Frame frame = this.frames.get(i);
            Action action = this.actions.get(i);

            frame.toBytes(buffer);
            buffer.writeBoolean(action != null);

            if (action != null)
            {
                buffer.writeByte(action.getType());
                action.toBytes(buffer);
            }
        }

        buffer.close();
    }

    /**
     * Read a recording from given file.
     *
     * This method basically checks if the given file has appropriate short
     * signature, and reads all frames and actions from the file.
     */
    public void fromBytes(File file) throws IOException, Exception
    {
        RandomAccessFile buffer = new RandomAccessFile(file, "rw");

        short signature = buffer.readShort();

        if (signature != SIGNATURE)
        {
            buffer.close();

            throw new Exception("Given file doesn't have appropriate signature!");
        }

        int frames = buffer.readInt();

        for (int i = 0; i < frames; i++)
        {
            Frame frame = new Frame();

            frame.fromBytes(buffer);

            if (buffer.readBoolean())
            {
                Action action = Action.fromType(buffer.readByte());

                action.fromBytes(buffer);
                this.actions.add(action);
            }
            else
            {
                this.actions.add(null);
            }

            this.frames.add(frame);
        }

        buffer.close();
    }
}