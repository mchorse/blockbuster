package mchorse.blockbuster.recording.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.MountingAction;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * This class stores actions and frames states for a recording (to be played
 * back or while recording).
 *
 * There's two list arrays in this class, index in both of these arrays
 * represents the frame position (0 is first frame). Frames list is always
 * populated, but actions list will contain some nulls.
 */
public class Record
{
    /**
     * Signature of the recording. If the first short of the record file isn't
     * this file, then the
     */
    public static final short SIGNATURE = 131;

    /**
     * Filename of this record
     */
    public String filename = "";

    /**
     * Delay between recording frames
     */
    public int delay = 1;

    /**
     * Recorded actions
     */
    public List<Action> actions = new ArrayList<Action>();

    /**
     * Recorded frames
     */
    public List<Frame> frames = new ArrayList<Frame>();

    /**
     * Unload timer. Used only on server side.
     */
    public int unload;

    public Record(String filename)
    {
        this.filename = filename;
        this.delay = Blockbuster.proxy.config.recording_delay;
        this.resetUnload();
    }

    /**
     * Get the length of this record in frames/ticks
     */
    public int getLength()
    {
        return Math.max(this.actions.size(), this.frames.size());
    }

    /**
     * Reset unloading timer
     */
    public void resetUnload()
    {
        this.unload = Blockbuster.proxy.config.record_unload_time;
    }

    /**
     * Apply a frame at given tick on the given actor. Don't pass tick value
     * less than 0, otherwise you might experience <s>tranquility</s> game
     * crash.
     */
    public void applyFrame(int tick, EntityActor actor, boolean force)
    {
        if (tick > this.frames.size())
        {
            return;
        }

        this.frames.get(tick).applyOnActor(actor, force);
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
     * Reset the actor based on this record
     */
    public void reset(EntityActor actor)
    {
        if (actor.isRiding())
        {
            this.resetMount(actor);
        }

        this.applyFrame(0, actor, true);
    }

    /**
     * Reset actor's mount
     */
    protected void resetMount(EntityActor actor)
    {
        int index = -1;

        /* Find at which tick player has mounted a vehicle */
        for (int i = 0, c = this.actions.size(); i < c; i++)
        {
            Action action = this.actions.get(i);

            if (action instanceof MountingAction)
            {
                MountingAction act = (MountingAction) action;

                if (act.isMounting)
                {
                    index = i + 1;
                    break;
                }
            }
        }

        actor.dismountEntity(actor.ridingEntity);

        if (index != -1)
        {
            Frame frame = this.frames.get(index);

            if (frame != null)
            {
                actor.ridingEntity.setPositionAndRotation(frame.x, frame.y, frame.z, frame.yaw, frame.pitch);
            }
        }
    }

    /**
     * Save a recording to given file.
     *
     * This method basically writes the signature of the current version,
     * and then saves all available frames and actions.
     */
    public void save(File file) throws IOException
    {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList frames = new NBTTagList();

        /* Version of the recording */
        compound.setShort("Version", SIGNATURE);
        compound.setByte("Delay", (byte) this.delay);

        int c = this.frames.size();
        int d = this.actions.size() - this.frames.size();

        if (d < 0) d = 0;

        for (int i = 0; i < c; i++)
        {
            if (d + 1 >= this.actions.size())
            {
                continue;
            }

            NBTTagCompound frameTag = new NBTTagCompound();

            Frame frame = this.frames.get(i);
            Action action = this.actions.get(d + i);

            frame.toNBT(frameTag);

            if (action != null)
            {
                NBTTagCompound actionTag = new NBTTagCompound();

                action.toNBT(actionTag);
                actionTag.setByte("Type", action.getType());

                frameTag.setTag("Action", actionTag);
            }

            frames.appendTag(frameTag);
        }

        compound.setTag("Frames", frames);

        CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file));
    }

    /**
     * Read a recording from given file.
     *
     * This method basically checks if the given file has appropriate short
     * signature, and reads all frames and actions from the file.
     */
    public void load(File file) throws IOException, Exception
    {
        NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
        short version = compound.getShort("Version");

        if (version != SIGNATURE)
        {
            throw new Exception("Given file doesn't have appropriate signature!");
        }

        this.delay = compound.getByte("Delay");

        NBTTagList frames = (NBTTagList) compound.getTag("Frames");

        for (int i = 0, c = frames.tagCount(); i < c; i++)
        {
            NBTTagCompound frameTag = frames.getCompoundTagAt(i);
            NBTTagCompound actionTag = (NBTTagCompound) frameTag.getTag("Action");
            Frame frame = new Frame();

            frame.fromNBT(frameTag);

            if (actionTag != null)
            {
                Action action = Action.fromType(actionTag.getByte("Type"));
                action.fromNBT(actionTag);

                this.actions.add(action);
            }
            else
            {
                this.actions.add(null);
            }

            this.frames.add(frame);
        }
    }
}