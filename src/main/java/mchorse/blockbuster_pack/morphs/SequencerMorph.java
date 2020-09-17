package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import mchorse.metamorph.api.morphs.utils.PausedMorph;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Sequencer morph
 * 
 * Next big thing since S&B, allows creating animated morphs with 
 * variable delays between changes
 */
public class SequencerMorph extends AbstractMorph implements IMorphProvider, ISyncableMorph
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
    public boolean isRandom;

    private PausedMorph pause = new PausedMorph();
    private Random random = new Random();

    public SequencerMorph()
    {
        super();

        this.name = "sequencer";
    }

    private Random getRandomSeed(float duration)
    {
        this.random.setSeed((long) (duration * 100000L));

        return this.random;
    }

    @Override
    public void pauseMorph(AbstractMorph previous, int offset)
    {
        this.pause.set(previous, offset);
    }

    @Override
    public boolean isPaused()
    {
        return this.pause.isPaused();
    }

    @Override
    public AbstractMorph getMorph()
    {
        return this.currentMorph.get();
    }

    public FoundMorph getMorphAt(int tick)
    {
        if (this.morphs.isEmpty())
        {
            return null;
        }

        float duration = 0;
        int size = this.morphs.size();

        for (SequenceEntry entry : this.morphs)
        {
            duration += entry.getDuration(this.getRandomSeed(duration));
        }

        if (duration <= 0)
        {
            return new FoundMorph(this.morphs.get(size - 1).morph, size == 1 ? null : this.morphs.get(size - 2).morph, 0, 0);
        }

        duration = 0;

        SequenceEntry entry = null;
        SequenceEntry lastEntry = null;
        int i = this.reverse ? size - 1 : 0;
        float lastDuration = 0;

        while (duration < tick)
        {
            lastDuration = duration;
            lastEntry = entry;

            entry = this.morphs.get(i);

            if (this.isRandom)
            {
                i = this.getRandomIndex(duration);
            }
            else
            {
                i = MathUtils.cycler(i + (this.reverse ? -1 : 1), 0, size - 1);
            }

            duration += entry.getDuration(this.getRandomSeed(duration));
        }

        return entry == null ? null : new FoundMorph(entry.morph, lastEntry == null ? null : lastEntry.morph, duration, lastDuration);
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
        this.updateCycle();

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
        if (!this.isPaused())
        {
            this.updateMorph(this.timer + partialTicks);
        }

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
        this.updateCycle();

        AbstractMorph morph = this.currentMorph.get();

        if (morph != null)
        {
            morph.update(target);
        }
    }

    /**
     * Update the cycle timer 
     */
    protected void updateCycle()
    {
        if (this.isPaused())
        {
            return;
        }

        this.updateMorph(this.timer);
        this.timer++;
    }

    /**
     * Update the current morph, make sure that we have currently the 
     * correct morph.
     */
    protected void updateMorph(float timer)
    {
        if (timer >= this.duration)
        {
            int size = this.morphs.size();

            if (this.isRandom)
            {
                this.current = this.getRandomIndex(this.duration);
            }
            else
            {
                this.current = MathUtils.cycler(this.current + (this.reverse ? -1 : 1), 0, size - 1);
            }

            if (this.current >= 0 && this.current < size)
            {
                SequenceEntry entry = this.morphs.get(this.current);
                AbstractMorph morph = MorphUtils.copy(entry.morph);

                if (this.isPaused())
                {
                    if (morph instanceof ISyncableMorph)
                    {
                        AbstractMorph previous = MorphUtils.copy(this.currentMorph.get());
                        int offset = (int) (timer - this.duration);

                        if (previous == null)
                        {
                            previous = this.pause.previous;
                        }

                        ((ISyncableMorph) morph).pauseMorph(previous, offset);
                    }

                    this.currentMorph.setDirect(morph);
                }
                else
                {
                    this.currentMorph.set(morph);
                }

                this.duration += entry.getDuration(this.getRandomSeed(this.duration));
            }

            if (!this.morphs.isEmpty())
            {
                boolean durationZero = this.morphs.get(this.current).duration == 0;

                if (this.timer >= this.duration && !durationZero)
                {
                    this.updateMorph(this.timer);
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
            this.isRandom = morph.isRandom;

            /* Runtime properties */
            this.currentMorph.copy(morph.currentMorph);
            this.timer = morph.timer;
            this.current = morph.current;
            this.duration = morph.duration;

            this.pause.copy(morph.pause);
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
            result = result && this.isRandom == seq.isRandom;
        }

        return result;
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
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

                this.current = -1;
                this.timer = 0;
                this.duration = this.morphs.isEmpty() ? 0 : this.morphs.get(0).duration;
                this.currentMorph.copy(sequencer.currentMorph);

                this.reverse = sequencer.reverse;
                this.isRandom = sequencer.isRandom;

                return true;
            }
        }

        return super.canMerge(morph);
    }

    @Override
    public void afterMerge(AbstractMorph morph)
    {
        super.afterMerge(morph);
        this.currentMorph.setDirect(morph);
        this.current = -1;
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
        if (this.isRandom) tag.setBoolean("Random", this.isRandom);

        this.pause.toNBT(tag);
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
                    this.current = -1;
                    this.currentMorph.set(MorphUtils.copy(morph));
                }

                this.morphs.add(entry);
            }
        }

        if (tag.hasKey("Reverse")) this.reverse = tag.getBoolean("Reverse");
        if (tag.hasKey("Random")) this.isRandom = tag.getBoolean("Random");

        this.pause.fromNBT(tag);

        if (this.isPaused())
        {
            this.current = -1;
            this.currentMorph.setDirect(null);
            this.duration = 0;
            this.timer = this.pause.offset;

            this.updateMorph(this.timer);
        }
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

        public float getDuration(Random random)
        {
            return this.duration + (this.random != 0 ? random.nextFloat() * this.random : 0);
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

        return this.get((int) (this.random.nextDouble() * this.morphs.size()));
    }

    public int getRandomIndex(float duration)
    {
        return (int) (this.getRandomSeed(duration * 2 + 5).nextFloat() * this.morphs.size());
    }

    public AbstractMorph get(int index)
    {
        if (index >= this.morphs.size() || index < 0)
        {
            return null;
        }

        return this.morphs.get(index).morph;
    }

    public static class FoundMorph
    {
        public AbstractMorph morph;
        public AbstractMorph previous;
        public int totalDuration;
        public int lastDuration;

        public FoundMorph(AbstractMorph morph, AbstractMorph previous, float totalDuration, float lastDuration)
        {
            this.morph = morph;
            this.previous = previous;
            this.totalDuration = (int) totalDuration;
            this.lastDuration = (int) lastDuration;
        }
    }
}