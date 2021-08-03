package mchorse.blockbuster.recording.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Frame chunk class
 *
 * This class is responsible for storing unfinished loading frames with
 * records from the client.
 */
public class FrameChunk
{
    /**
     * List of frame lists. The list to store them all...
     */
    public List<List<Frame>> frames;

    /**
     * How much chunks this chunk should store.
     */
    public int count;

    /**
     * Recording offset
     */
    public int offset;

    public FrameChunk(int count, int offset)
    {
        this.frames = new ArrayList<List<Frame>>(count);
        this.count = count;
        this.offset = offset;

        for (int i = 0; i < count; i++)
        {
            this.frames.add(null);
        }
    }

    /**
     * Does this chunked storage is fully filled
     */
    public boolean isFilled()
    {
        for (int i = 0; i < this.count; i++)
        {
            if (this.frames.get(i) == null)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Add chunked frames to the frames storage
     */
    public void add(int index, List<Frame> frames)
    {
        this.frames.set(index, frames);
    }

    /**
     * Compile all frames into one list
     */
    public List<Frame> compile(List<Frame> oldFrames)
    {
        List<Frame> output = new ArrayList<Frame>();

        if (this.offset > 0)
        {
            List<Frame> merged = new ArrayList<Frame>();

            for (List<Frame> frames : this.frames)
            {
                merged.addAll(frames);
            }

            int newSize = this.offset + merged.size();

            for (int i = 0, c = Math.max(newSize, oldFrames.size()); i < c; i++)
            {
                Frame frame;

                if (i < this.offset)
                {
                    frame = i < oldFrames.size() ? oldFrames.get(i) : merged.get(0).copy();
                }
                else if (i > newSize)
                {
                    frame = oldFrames.get(i);
                }
                else
                {
                    int index = i - this.offset;

                    frame =  index < merged.size() ? merged.get(index) : oldFrames.get(i);
                }

                output.add(frame);
            }
        }
        else
        {
            for (List<Frame> frames : this.frames)
            {
                output.addAll(frames);
            }
        }

        return output;
    }
}