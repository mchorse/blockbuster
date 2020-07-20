package mchorse.blockbuster_pack.morphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Sequencer morph
 * 
 * Next big thing since S&B, allows creating animated morphs with 
 * variable delays between changes
 */
public class SequencerMorph extends AbstractMorph implements IMorphProvider
{
    /**
     * List of sequence entries (morph and their delay) 
     */
    public List<SequenceEntry> morphs = new ArrayList<SequenceEntry>();

    /**
     * Current morph 
     */
    public Morph currentMorph = new Morph();

    /**
     * Index of current cell 
     */
    public int current;

    /**
     * Timer on which depends the cycling
     */
    public int timer;

    /**
     * Duration of the current  
     */
    public float duration;

    /**
     * Reverse playback 
     */
    public boolean reverse;

    /**
     * Random order of sequencer playback
     */
    public boolean random;

    public SequencerMorph()
    {
        super();

        this.name = "sequencer";
    }

    @Override
    public AbstractMorph getMorph()
    {
        return this.currentMorph.get();
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        return I18n.format("blockbuster.morph.sequencer");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        this.updateCycle(true);

        if (this.morphs.isEmpty())
        {
            GlStateManager.color(1, 1, 1);
            BBIcons.CHICKEN.render(x - 8, y - 20);
        }

        AbstractMorph morph = this.currentMorph.get();

        if (morph != null)
        {
            morph.renderOnScreen(player, x, y, scale, alpha);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.updateMorph(this.timer + partialTicks, true);

        AbstractMorph morph = this.currentMorph.get();

        if (morph != null)
        {
            morph.render(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        AbstractMorph morph = this.currentMorph.get();

        if (morph != null)
        {
            return morph.renderHand(player, hand);
        }

        return false;
    }

    @Override
    public void update(EntityLivingBase target)
    {
        this.updateCycle(target.world.isRemote);

        AbstractMorph morph = this.currentMorph.get();

        if (morph != null)
        {
            morph.update(target);
        }
    }

    /**
     * Update the cycle timer 
     */
    protected void updateCycle(boolean isRemote)
    {
        this.updateMorph(this.timer, isRemote);
        this.timer++;
    }

    /**
     * Update the current morph, make sure that we have currently the 
     * correct morph.
     */
    protected void updateMorph(float timer, boolean isRemote)
    {
        if (timer >= this.duration)
        {
            int size = this.morphs.size();

            if (this.random)
            {
                this.current = (int) (Math.random() * size);
                this.timer = 0;
                this.duration = 0;
            }
            else
            {
                this.current += this.reverse ? -1 : 1;

                if (this.current >= size)
                {
                    this.current = 0;
                    this.timer = 0;
                    this.duration = 0;
                }
                else if (this.current < 0)
                {
                    this.current = size - 1;
                    this.timer = 0;
                    this.duration = 0;
                }
            }

            if (this.current >= 0 && this.current < size)
            {
                SequenceEntry entry = this.morphs.get(this.current);
                AbstractMorph morph = MorphUtils.copy(entry.morph);

                this.currentMorph.set(morph);
                this.duration += entry.getDuration();
            }

            if (!this.morphs.isEmpty())
            {
                boolean durationZero = this.morphs.get(this.current).duration == 0;

                if (this.timer >= this.duration && !durationZero)
                {
                    this.updateMorph(this.timer, isRemote);
                }
            }
        }
    }

    @Override
    public AbstractMorph create()
    {
        return new SequencerMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof SequencerMorph)
        {
            SequencerMorph morph = (SequencerMorph) from;

            for (SequenceEntry entry : morph.morphs)
            {
                this.morphs.add(entry.clone());
            }

            this.reverse = morph.reverse;
            this.random = morph.random;

            /* Runtime properties */
            this.currentMorph.copy(morph.currentMorph);
            this.timer = morph.timer;
            this.current = morph.current;
            this.duration = morph.duration;
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        AbstractMorph morph = this.currentMorph.get();

        return morph == null ? 0 : morph.getWidth(target);
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        AbstractMorph morph = this.currentMorph.get();

        return morph == null ? 0 : morph.getHeight(target);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof SequencerMorph)
        {
            SequencerMorph seq = (SequencerMorph) obj;

            result = result && Objects.equals(this.morphs, seq.morphs);
            result = result && this.reverse == seq.reverse;
            result = result && this.random == seq.random;
        }

        return result;
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
        if (morph instanceof CustomMorph)
        {
            AbstractMorph current = this.currentMorph.get();

            if (current instanceof CustomMorph)
            {
                CustomMorph customMorph = (CustomMorph) current;
                CustomMorph custom = (CustomMorph) morph;

                ModelPose pose = customMorph.getCurrentPose();

                if (customMorph.animation.isInProgress() && pose != null)
                {
                    custom.animation.last = customMorph.animation.calculatePose(pose, 1).clone();
                }
                else
                {
                    custom.animation.last = pose;
                }

                return false;
            }
        }

        if (morph instanceof SequencerMorph)
        {
            SequencerMorph sequencer = (SequencerMorph) morph;

            if (!sequencer.morphs.equals(this.morphs))
            {
                this.morphs.clear();

                for (SequenceEntry entry : sequencer.morphs)
                {
                    this.morphs.add(entry.clone());
                }

                this.current = 0;
                this.timer = 0;
                this.duration = this.morphs.isEmpty() ? 0 : this.morphs.get(0).duration;
                this.currentMorph.copy(sequencer.currentMorph);

                this.reverse = sequencer.reverse;
                this.random = sequencer.random;

                return true;
            }
        }

        return super.canMerge(morph);
    }

    @Override
    public void reset()
    {
        super.reset();

        this.timer = this.current = 0;
        this.duration = 0;
        this.reverse = false;
        this.currentMorph.setDirect(null);
        this.morphs.clear();
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (!this.morphs.isEmpty())
        {
            NBTTagList list = new NBTTagList();

            for (SequenceEntry entry : this.morphs)
            {
                NBTTagCompound entryTag = new NBTTagCompound();

                if (entry.morph != null)
                {
                    NBTTagCompound morphTag = new NBTTagCompound();

                    entry.morph.toNBT(morphTag);
                    entryTag.setTag("Morph", morphTag);
                }

                entryTag.setFloat("Duration", entry.duration);
                entryTag.setFloat("Random", entry.random);
                list.appendTag(entryTag);
            }

            tag.setTag("List", list);
        }

        if (this.reverse) tag.setBoolean("Reverse", this.reverse);
        if (this.random) tag.setBoolean("Random", this.random);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("List", NBT.TAG_LIST))
        {
            NBTTagList list = tag.getTagList("List", NBT.TAG_COMPOUND);

            for (int i = 0, c = list.tagCount(); i < c; i++)
            {
                NBTTagCompound morphTag = list.getCompoundTagAt(i);
                AbstractMorph morph = null;

                if (morphTag.hasKey("Morph", NBT.TAG_COMPOUND))
                {
                    morph = MorphManager.INSTANCE.morphFromNBT(morphTag.getCompoundTag("Morph"));
                }

                SequenceEntry entry = new SequenceEntry(morph);

                if (morphTag.hasKey("Duration", NBT.TAG_ANY_NUMERIC))
                {
                    entry.duration = morphTag.getFloat("Duration");
                }

                if (morphTag.hasKey("Random", NBT.TAG_ANY_NUMERIC))
                {
                    entry.random = morphTag.getFloat("Random");
                }

                if (i == 0)
                {
                    this.duration = entry.getDuration();
                    this.currentMorph.set(MorphUtils.copy(morph));
                }

                this.morphs.add(entry);
            }
        }

        if (tag.hasKey("Reverse")) this.reverse = tag.getBoolean("Reverse");
        if (tag.hasKey("Random")) this.random = tag.getBoolean("Random");
    }

    /**
     * Sequence entry
     * 
     * Represents a data class cell/entry thing that stores morph and 
     * its delay/duration until next morph in the sequence.
     */
    public static class SequenceEntry
    {
        public AbstractMorph morph;
        public float duration = 10;
        public float random = 0;

        public SequenceEntry(AbstractMorph morph)
        {
            this.morph = morph;
        }

        public SequenceEntry(AbstractMorph morph, float duration)
        {
            this(morph, duration, 0);
        }

        public SequenceEntry(AbstractMorph morph, float duration, float random)
        {
            this.morph = morph;
            this.duration = duration;
            this.random = random;
        }

        public float getDuration()
        {
            return this.duration + (this.random != 0 ? (float) Math.random() * this.random : 0);
        }

        @Override
        public SequenceEntry clone()
        {
            return new SequenceEntry(this.morph, this.duration, this.random);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof SequenceEntry)
            {
                SequenceEntry entry = (SequenceEntry) obj;

                return this.duration == entry.duration && this.random == entry.random && Objects.equals(this.morph, entry.morph);
            }

            return super.equals(obj);
        }
    }

    public AbstractMorph getRandom()
    {
        if (this.morphs.isEmpty())
        {
            return null;
        }

        return this.get((int) (Math.random() * this.morphs.size()));
    }

    public AbstractMorph get(int index)
    {
        if (index >= this.morphs.size() || index < 0)
        {
            return null;
        }

        return this.morphs.get(index).morph;
    }
}