package mchorse.blockbuster.recording.data;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster.recording.actions.MountingAction;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public static final FoundAction ACTION = new FoundAction();
    public static final MorphAction MORPH = new MorphAction();

    /**
     * Signature of the recording. If the first short of the record file isn't
     * this file, then the
     */
    public static final short SIGNATURE = 148;

    /**
     * Filename of this record
     */
    public String filename;

    /**
     * Version of this record
     */
    public short version = SIGNATURE;

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
     * Get actions on given tick
     */
    public List<Action> getActions(int tick)
    {
        if (tick >= this.actions.size() || tick < 0)
        {
            return null;
        }

        return this.actions.get(tick);
    }

    /**
     * Get an action on given tick and index 
     */
    public Action getAction(int tick, int index)
    {
        List<Action> actions = this.getActions(tick);

        if (actions != null && index >= 0 && index < actions.size())
        {
            return actions.get(index);
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
        this.unload = Blockbuster.recordUnloadTime.get();
    }

    public void applyFrame(int tick, EntityLivingBase actor, boolean force)
    {
        this.applyFrame(tick, actor, force, false);
    }

    /**
     * Apply a frame at given tick on the given actor. 
     */
    public void applyFrame(int tick, EntityLivingBase actor, boolean force, boolean realPlayer)
    {
        if (tick >= this.frames.size() || tick < 0)
        {
            return;
        }

        Frame frame = this.frames.get(tick);

        frame.apply(actor, force);

        if (realPlayer)
        {
            actor.setLocationAndAngles(frame.x, frame.y, frame.z, frame.yaw, frame.pitch);
            actor.motionX = frame.motionX;
            actor.motionY = frame.motionY;
            actor.motionZ = frame.motionZ;

            actor.onGround = frame.onGround;

            if (frame.hasBodyYaw)
            {
                actor.renderYawOffset = frame.bodyYaw;
            }

            if (actor.world.isRemote)
            {
                this.applyClientMovement(actor, frame);
            }

            actor.setSneaking(frame.isSneaking);
            actor.setSprinting(frame.isSprinting);

            if (actor.world.isRemote)
            {
                this.applyFrameClient(actor, null, frame);
            }
        }

        if (actor.world.isRemote && Blockbuster.actorFixY.get())
        {
            actor.posY = frame.y;
        }

        Frame prev = this.frames.get(Math.max(0, tick - 1));

        if (realPlayer || !actor.world.isRemote)
        {
            actor.lastTickPosX = prev.x;
            actor.lastTickPosY = prev.y;
            actor.lastTickPosZ = prev.z;
            actor.prevPosX = prev.x;
            actor.prevPosY = prev.y;
            actor.prevPosZ = prev.z;

            actor.prevRotationYaw = prev.yaw;
            actor.prevRotationPitch = prev.pitch;
            actor.prevRotationYawHead = prev.yawHead;

            if (prev.hasBodyYaw)
            {
                actor.prevRenderYawOffset = prev.bodyYaw;
            }

            if (actor.world.isRemote)
            {
                this.applyFrameClient(actor, prev, frame);
            }
        }
        else if (actor instanceof EntityActor)
        {
            ((EntityActor) actor).prevRoll = prev.roll;
        }

        /* Override fall distance, apparently fallDistance gets reset
         * faster than RecordRecorder can record both onGround and
         * fallDistance being correct for player, so we just hack */
        actor.fallDistance = prev.fallDistance;

        if (tick < this.frames.size() - 1)
        {
            Frame next = this.frames.get(tick + 1);

            /* Walking sounds */
            if (actor instanceof EntityPlayer)
            {
                double dx = next.x - frame.x;
                double dy = next.y - frame.y;
                double dz = next.z - frame.z;

                actor.distanceWalkedModified = actor.distanceWalkedModified + MathHelper.sqrt(dx * dx + dz * dz) * 0.32F;
                actor.distanceWalkedOnStepModified = actor.distanceWalkedOnStepModified + MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 0.32F;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void applyClientMovement(EntityLivingBase actor, Frame frame)
    {
        if (actor instanceof EntityPlayerSP)
        {
            MovementInput input = ((EntityPlayerSP) actor).movementInput;

            input.sneak = frame.isSneaking;
        }
    }

    @SideOnly(Side.CLIENT)
    private void applyFrameClient(EntityLivingBase actor, Frame prev, Frame frame)
    {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (actor == player)
        {
            CameraHandler.setRoll(prev == null ? frame.roll : prev.roll, frame.roll);
        }
    }

    public Frame getFrameSafe(int tick)
    {
        if (this.frames.isEmpty())
        {
            return null;
        }

        return this.frames.get(MathUtils.clamp(tick, 0, this.frames.size() - 1));
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

                try
                {
                    action.apply(actor);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Seek the nearest morph action
     */
    public FoundAction seekMorphAction(int tick, MorphAction last)
    {
        /* I hope it won't cause a lag...  */
        int threshold = 0;

        boolean canRet = last == null;

        while (tick >= threshold)
        {
            List<Action> actions = this.actions.get(tick);

            if (actions == null)
            {
                tick--;

                continue;
            }

            for (int i = actions.size() - 1; i >= 0; i--)
            {
                Action action = actions.get(i);

                if (!canRet && action == last)
                {
                    canRet = true;
                }
                else if (canRet && action instanceof MorphAction)
                {
                    ACTION.set(tick, (MorphAction) action);

                    return ACTION;
                }
            }

            tick--;
        }

        return null;
    }

    /**
     * Apply previous morph
     */
    public void applyPreviousMorph(EntityLivingBase actor, Replay replay, int tick, MorphType type)
    {
        boolean pause = type != MorphType.REGULAR && Blockbuster.recordPausePreview.get();
        AbstractMorph replayMorph = replay == null ? null : replay.morph;

        /* when the tick is at the end - do not apply replay's morph - stay at the last morph */
        if (tick >= this.actions.size()) return;

        FoundAction found = this.seekMorphAction(tick, null);

        if (found != null)
        {
            try
            {
                MorphAction action = found.action;

                if (pause && action.morph instanceof ISyncableMorph)
                {
                    int foundTick = found.tick;
                    int offset = tick - foundTick;

                    found = this.seekMorphAction(foundTick, action);
                    AbstractMorph previous = found == null ? replayMorph : found.action.morph;
                    int previousOffset = foundTick - (found == null ? 0 : found.tick);

                    action.applyWithOffset(actor, offset, previous, previousOffset, type == MorphType.FORCE);
                }
                else
                {
                    action.apply(actor);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (replay != null)
        {
            if (pause && replay.morph != null)
            {
                MORPH.morph = replay.morph;
                MORPH.applyWithOffset(actor, tick, null, 0, type == MorphType.FORCE);
            }
            else if (type == MorphType.FORCE && replay.morph != null)
            {
                MORPH.morph = replay.morph;
                MORPH.applyWithForce(actor);
            }
            else
            {
                replay.apply(actor);
            }
        }
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
            actor.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
            actor.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemStack.EMPTY);
            actor.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemStack.EMPTY);
            actor.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemStack.EMPTY);
            actor.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
            actor.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
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

    public void addActions(int tick, List<Action> actions)
    {
        if (tick < 0 || tick >= this.actions.size())
        {
            return;
        }

        List<Action> present = this.actions.get(tick);

        if (present == null)
        {
            this.actions.set(tick, actions);
        }
        else if (actions != null)
        {
            present.addAll(actions);
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
     * Replace an action at given tick and index
     */
    public void replaceAction(int tick, int index, Action action)
    {
        if (tick < 0 || tick >= this.actions.size())
        {
            return;
        }

        List<Action> actions = this.actions.get(tick);

        if (actions == null || index < 0 || index >= actions.size())
        {
            this.addAction(tick, action);
        }
        else
        {
            actions.set(index, action);
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
        record.preDelay = this.preDelay;
        record.postDelay = this.postDelay;

        for (Frame frame : this.frames)
        {
            record.frames.add(frame.copy());
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

    public void save(File file) throws IOException
    {
        this.save(file, true);
    }

    /**
     * Save a recording to given file.
     *
     * This method basically writes the signature of the current version,
     * and then saves all available frames and actions.
     */
    public void save(File file, boolean savePast) throws IOException
    {
        if (savePast && file.isFile())
        {
            this.savePastCopies(file);
        }

        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList frames = new NBTTagList();

        /* Version of the recording */
        compound.setShort("Version", SIGNATURE);
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
        this.load(CompressedStreamTools.readCompressed(new FileInputStream(file)));
    }

    public void load(NBTTagCompound compound)
    {
        NBTTagCompound map = null;

        this.version = compound.getShort("Version");
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

    public void fillMissingActions()
    {
        while (this.actions.size() < this.frames.size())
        {
            this.actions.add(null);
        }
    }

    public static class FoundAction
    {
        public int tick;
        public MorphAction action;

        public void set(int tick, MorphAction action)
        {
            this.tick = tick;
            this.action = action;
        }
    }

    public static enum MorphType
    {
        REGULAR, PAUSE, FORCE
    }
}