package mchorse.blockbuster.recording.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.director.Replay;
import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster.recording.actions.MountingAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
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
    public static final short SIGNATURE = 148;

    /**
     * Filename of this record
     */
    public String filename = "";

    /**
     * Version of this record
     */
    public short version = SIGNATURE;

    /**
     * Delay between recording frames
     */
    public int delay = 1;

    /**
     * Pre-delay same thing as post-delay but less useful
     */
    public int preDelay = 0;

    /**
     * Post-delay allows actors to stay longer on the screen before 
     * whoosing into void
     */
    public int postDelay = 0;

    /**
     * Recorded actions
     */
    public List<List<Action>> actions = new ArrayList<List<Action>>();

    /**
     * Recorded frames
     */
    public List<Frame> frames = new ArrayList<Frame>();

    /**
     * Player data which was recorded when player started recording 
     */
    public NBTTagCompound playerData;

    /**
     * Unload timer. Used only on server side.
     */
    public int unload;

    /**
     * Whether this record has changed elements
     */
    public boolean dirty;

    public Record(String filename)
    {
        this.filename = filename;
        this.delay = Blockbuster.proxy.config.recording_delay;
        this.resetUnload();
    }

    /**
     * Get the full length (including post and pre delays) of this record in frames/ticks
     */
    public int getFullLength()
    {
        return this.preDelay + this.getLength() + this.postDelay;
    }

    /**
     * Get the length of this record in frames/ticks
     */
    public int getLength()
    {
        return Math.max(this.actions.size(), this.frames.size());
    }

    /**
     * Get an action on given tick and index 
     */
    public Action getAction(int tick, int index)
    {
        if (tick >= 0 && tick < this.actions.size())
        {
            List<Action> actions = this.actions.get(tick);

            if (actions != null && index >= 0 && index < actions.size())
            {
                return actions.get(index);
            }
        }

        return null;
    }

    /**
     * Get frame on given tick 
     */
    public Frame getFrame(int tick)
    {
        if (tick >= this.frames.size() || tick < 0)
        {
            return null;
        }

        return this.frames.get(tick);
    }

    /**
     * Reset unloading timer
     */
    public void resetUnload()
    {
        this.unload = Blockbuster.proxy.config.record_unload_time;
    }

    /**
     * Apply a frame at given tick on the given actor. 
     */
    public void applyFrame(int tick, EntityLivingBase actor, boolean force)
    {
        if (tick >= this.frames.size() || tick < 0)
        {
            return;
        }

        Frame frame = this.frames.get(tick);

        frame.apply(actor, force);

        if (actor.worldObj.isRemote && Blockbuster.proxy.config.actor_y)
        {
            actor.posY = frame.y;
        }

        if (tick != 0)
        {
            Frame prev = this.frames.get(tick - 1);

            /* Override fall distance, apparently fallDistance gets reset
             * faster than RecordRecorder can record both onGround and
             * fallDistance being correct for player, so we just hack */
            actor.fallDistance = prev.fallDistance;

            /* TODO: do something about it, it literally fixes the issue with 
             * floating actors, but at the cost of making the movement very 
             * sharp instead of smooth */
            if (actor.worldObj.isRemote)
            {
                /* actor.lastTickPosY = actor.prevPosY = prev.y; */
            }
        }
    }

    /**
     * Apply an action at the given tick on the given actor. Don't pass tick
     * value less than 0, otherwise you might experience game crash.
     */
    public void applyAction(int tick, EntityLivingBase actor)
    {
        this.applyAction(tick, actor, false);
    }

    /**
     * Apply an action at the given tick on the given actor. Don't pass tick
     * value less than 0, otherwise you might experience game crash.
     */
    public void applyAction(int tick, EntityLivingBase actor, boolean safe)
    {
        if (tick >= this.actions.size() || tick < 0)
        {
            return;
        }

        List<Action> actions = this.actions.get(tick);

        if (actions != null)
        {
            for (Action action : actions)
            {
                if (safe && !action.isSafe())
                {
                    continue;
                }

                action.apply(actor);
            }
        }
    }

    /**
     * Seek the nearest morph action
     */
    public void seekMorphAction(EntityLivingBase actor, int tick, Replay replay)
    {
        if (tick >= this.actions.size())
        {
            return;
        }

        /* I hope it won't cause a lag...  */
        int threshold = 0;

        while (tick > threshold)
        {
            List<Action> actions = this.actions.get(tick);

            if (actions == null)
            {
                tick--;

                continue;
            }

            for (Action action : actions)
            {
                if (action instanceof MorphAction)
                {
                    action.apply(actor);
                    return;
                }
            }

            tick--;
        }

        replay.apply(actor);
    }

    /**
     * Reset the actor based on this record
     */
    public void reset(EntityLivingBase actor)
    {
        if (actor.isRiding())
        {
            this.resetMount(actor);
        }

        if (actor.getHealth() > 0.0F)
        {
            this.applyFrame(0, actor, true);

            /* Reseting actor's state */
            actor.setSneaking(false);
            actor.setSprinting(false);
            actor.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
            actor.setItemStackToSlot(EntityEquipmentSlot.CHEST, null);
            actor.setItemStackToSlot(EntityEquipmentSlot.LEGS, null);
            actor.setItemStackToSlot(EntityEquipmentSlot.FEET, null);
            actor.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
            actor.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
        }
    }

    /**
     * Reset actor's mount
     */
    protected void resetMount(EntityLivingBase actor)
    {
        int index = -1;

        /* Find at which tick player has mounted a vehicle */
        for (int i = 0, c = this.actions.size(); i < c; i++)
        {
            List<Action> actions = this.actions.get(i);

            if (actions == null)
            {
                continue;
            }

            for (Action action : actions)
            {
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
        }

        actor.dismountRidingEntity();

        if (index != -1)
        {
            Frame frame = this.frames.get(index);

            if (frame != null)
            {
                Entity mount = actor.getRidingEntity();

                if (mount != null && !(mount instanceof EntityActor))
                {
                    mount.setPositionAndRotation(frame.x, frame.y, frame.z, frame.yaw, frame.pitch);
                }
            }
        }
    }

    /**
     * Add an action to the record
     */
    public void addAction(int tick, Action action)
    {
        List<Action> actions = this.actions.get(tick);

        if (actions != null)
        {
            actions.add(action);
        }
        else
        {
            actions = new ArrayList<Action>();
            actions.add(action);

            this.actions.set(tick, actions);
        }
    }

    /**
     * Add an action to the record
     */
    public void addAction(int tick, int index, Action action)
    {
        List<Action> actions = this.actions.get(tick);

        if (actions != null)
        {
            if (index == -1)
            {
                actions.add(action);
            }
            else
            {
                actions.add(index, action);
            }
        }
        else
        {
            actions = new ArrayList<Action>();
            actions.add(action);

            this.actions.set(tick, actions);
        }
    }

    /**
     * Remove an action at given tick and index 
     */
    public void removeAction(int tick, int index)
    {
        if (index == -1)
        {
            this.actions.set(tick, null);
        }
        else
        {
            List<Action> actions = this.actions.get(tick);

            if (index >= 0 && index < actions.size())
            {
                actions.remove(index);

                if (actions.isEmpty())
                {
                    this.actions.set(tick, null);
                }
            }
        }
    }

    /**
     * Create a copy of this record
     */
    @Override
    public Record clone()
    {
        Record record = new Record(this.filename);

        record.version = this.version;
        record.delay = this.delay;
        record.preDelay = this.preDelay;
        record.postDelay = this.postDelay;

        for (Frame frame : this.frames)
        {
            record.frames.add(frame.clone());
        }

        for (List<Action> actions : this.actions)
        {
            if (actions == null || actions.isEmpty())
            {
                record.actions.add(null);
            }
            else
            {
                List<Action> newActions = new ArrayList<Action>();

                for (Action action : actions)
                {
                    try
                    {
                        NBTTagCompound tag = new NBTTagCompound();

                        action.toNBT(tag);

                        Action newAction = ActionRegistry.fromType(ActionRegistry.getType(action));

                        newAction.fromNBT(tag);
                        newActions.add(newAction);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Failed to clone an action!");
                        e.printStackTrace();
                    }
                }

                record.actions.add(newActions);
            }
        }

        return record;
    }

    /**
     * Save a recording to given file.
     *
     * This method basically writes the signature of the current version,
     * and then saves all available frames and actions.
     */
    public void save(File file) throws IOException
    {
        if (file.isFile())
        {
            this.savePastCopies(file);
        }

        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList frames = new NBTTagList();

        /* Version of the recording */
        compound.setShort("Version", SIGNATURE);
        compound.setByte("Delay", (byte) this.delay);
        compound.setInteger("PreDelay", this.preDelay);
        compound.setInteger("PostDelay", this.postDelay);
        compound.setTag("Actions", this.createActionMap());

        if (this.playerData != null)
        {
            compound.setTag("PlayerData", this.playerData);
        }

        int c = this.frames.size();
        int d = this.actions.size() - this.frames.size();

        if (d < 0) d = 0;

        for (int i = 0; i < c; i++)
        {
            NBTTagCompound frameTag = new NBTTagCompound();

            Frame frame = this.frames.get(i);
            List<Action> actions = null;

            if (d + i <= this.actions.size() - 1)
            {
                actions = this.actions.get(d + i);
            }

            frame.toNBT(frameTag);

            if (actions != null)
            {
                NBTTagList actionsTag = new NBTTagList();

                for (Action action : actions)
                {
                    NBTTagCompound actionTag = new NBTTagCompound();

                    action.toNBT(actionTag);
                    actionTag.setByte("Type", ActionRegistry.CLASS_TO_ID.get(action.getClass()));
                    actionsTag.appendTag(actionTag);
                }

                frameTag.setTag("Action", actionsTag);
            }

            frames.appendTag(frameTag);
        }

        compound.setTag("Frames", frames);

        CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file));
    }

    /**
     * This method removes the last file, and renames past versions of a recording files. 
     * This should save countless hours of work in case somebody accidentally overwrote 
     * a player recording.
     */
    private void savePastCopies(File file)
    {
        final int copies = 5;

        int counter = copies;
        String name = FilenameUtils.removeExtension(file.getName());

        while (counter >= 0 && file.exists())
        {
            File current = this.getPastFile(file, name, counter);

            if (current.exists()) 
            {
                if (counter == copies)
                {
                    current.delete();
                }
                else
                {
                    File previous = this.getPastFile(file, name, counter + 1);

                    current.renameTo(previous);
                }
            }

            counter--;
        }
    }

    /**
     * Get a path to the past copy of the file
     */
    private File getPastFile(File file, String name, int iteration)
    {
        return new File(file.getParentFile(), name + (iteration == 0 ? ".dat" : ".dat~" + iteration));
    }

    /**
     * Creates an action map between action name and an action type byte values
     * for compatibility
     */
    private NBTTagCompound createActionMap()
    {
        NBTTagCompound tag = new NBTTagCompound();

        for (Map.Entry<String, Byte> entry : ActionRegistry.NAME_TO_ID.entrySet())
        {
            tag.setString(entry.getValue().toString(), entry.getKey());
        }

        return tag;
    }

    /**
     * Read a recording from given file.
     *
     * This method basically checks if the given file has appropriate short
     * signature, and reads all frames and actions from the file.
     */
    public void load(File file) throws IOException
    {
        NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
        NBTTagCompound map = null;

        this.version = compound.getShort("Version");
        this.delay = compound.getByte("Delay");
        this.preDelay = compound.getInteger("PreDelay");
        this.postDelay = compound.getInteger("PostDelay");

        if (compound.hasKey("Actions", 10))
        {
            map = compound.getCompoundTag("Actions");
        }

        if (compound.hasKey("PlayerData", 10))
        {
            this.playerData = compound.getCompoundTag("PlayerData");
        }

        NBTTagList frames = (NBTTagList) compound.getTag("Frames");

        for (int i = 0, c = frames.tagCount(); i < c; i++)
        {
            NBTTagCompound frameTag = frames.getCompoundTagAt(i);
            NBTBase actionTag = frameTag.getTag("Action");
            Frame frame = new Frame();

            frame.fromNBT(frameTag);

            if (actionTag != null)
            {
                try
                {
                    List<Action> actions = new ArrayList<Action>();

                    if (actionTag instanceof NBTTagCompound)
                    {
                        Action action = this.actionFromNBT((NBTTagCompound) actionTag, map);

                        if (action != null)
                        {
                            actions.add(action);
                        }
                    }
                    else if (actionTag instanceof NBTTagList)
                    {
                        NBTTagList list = (NBTTagList) actionTag;

                        for (int ii = 0, cc = list.tagCount(); ii < cc; ii++)
                        {
                            Action action = this.actionFromNBT(list.getCompoundTagAt(ii), map);

                            if (action != null)
                            {
                                actions.add(action);
                            }
                        }
                    }

                    this.actions.add(actions);
                }
                catch (Exception e)
                {
                    System.out.println("Failed to load an action at frame " + i);
                    e.printStackTrace();
                }
            }
            else
            {
                this.actions.add(null);
            }

            this.frames.add(frame);
        }
    }

    private Action actionFromNBT(NBTTagCompound tag, NBTTagCompound map) throws Exception
    {
        byte type = tag.getByte("Type");
        Action action = null;

        if (map == null)
        {
            action = ActionRegistry.fromType(type);
        }
        else
        {
            String name = map.getString(String.valueOf(type));

            if (ActionRegistry.NAME_TO_CLASS.containsKey(name))
            {
                action = ActionRegistry.fromName(name);
            }
        }

        if (action != null)
        {
            action.fromNBT(tag);
        }

        return action;
    }

    public void reverse()
    {
        Collections.reverse(this.frames);
        Collections.reverse(this.actions);
    }
}