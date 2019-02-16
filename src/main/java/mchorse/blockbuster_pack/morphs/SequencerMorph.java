package mchorse.blockbuster_pack.morphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
public class SequencerMorph extends AbstractMorph
{
    /**
     * List of sequence entries (morph and their delay) 
     */
    public List<SequenceEntry> morphs = new ArrayList<SequenceEntry>();

    /**
     * Current morph 
     */
    public AbstractMorph currentMorph;

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
    public int duration;

    /**
     * Reverse playback 
     */
    public boolean reverse;

    public SequencerMorph()
    {
        this.name = "sequencer";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        this.updateCycle();

        if (this.morphs.isEmpty())
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.currentScreen != null)
            {
                GlStateManager.color(1, 1, 1);
                GlStateManager.enableAlpha();
                mc.renderEngine.bindTexture(GuiDashboard.ICONS);
                /* Fuck you, Gui class, for not making the methods static */
                mc.currentScreen.drawTexturedModalRect(x - 8, y - 20, 32, 16, 16, 16);
                GlStateManager.disableAlpha();
            }
        }

        if (this.currentMorph != null)
        {
            this.currentMorph.renderOnScreen(player, x, y, scale, alpha);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (this.currentMorph != null)
        {
            this.currentMorph.render(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        if (this.currentMorph != null)
        {
            return this.currentMorph.renderHand(player, hand);
        }

        return false;
    }

    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        this.updateCycle();

        if (this.currentMorph != null)
        {
            this.currentMorph.update(target, cap);
        }
    }

    /**
     * Update the cycle timer 
     */
    protected void updateCycle()
    {
        if (this.timer++ >= this.duration)
        {
            int size = this.morphs.size();

            this.current += this.reverse ? -1 : 1;

            if (this.current >= size)
            {
                this.current = 0;
            }
            else if (this.current < 0)
            {
                this.current = size - 1;
            }

            if (this.current >= 0 && this.current < size)
            {
                SequenceEntry entry = this.morphs.get(this.current);

                this.currentMorph = entry.morph;
                this.duration = entry.duration;
            }

            this.timer = 0;
        }
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        SequencerMorph morph = new SequencerMorph();

        morph.name = this.name;
        morph.settings = this.settings;

        for (SequenceEntry entry : this.morphs)
        {
            morph.morphs.add(entry.clone());
        }

        morph.reverse = this.reverse;

        return morph;
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return this.currentMorph != null ? this.currentMorph.getWidth(target) : 0;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return this.currentMorph != null ? this.currentMorph.getHeight(target) : 0;
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
        }

        return result;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.timer = this.current = this.duration = 0;
        this.reverse = false;
        this.currentMorph = null;
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

                entryTag.setInteger("Duration", entry.duration);
                list.appendTag(entryTag);
            }

            tag.setTag("List", list);
        }

        if (this.reverse) tag.setBoolean("Reverse", this.reverse);
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
                    entry.duration = morphTag.getInteger("Duration");
                }

                this.morphs.add(entry);
            }
        }

        this.reverse = tag.getBoolean("Reverse");
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
        public int duration = 5;

        public SequenceEntry(AbstractMorph morph)
        {
            this.morph = morph;
        }

        public SequenceEntry(AbstractMorph morph, int duration)
        {
            this.morph = morph;
            this.duration = duration;
        }

        @Override
        public SequenceEntry clone()
        {
            return new SequenceEntry(this.morph, this.duration);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof SequenceEntry)
            {
                SequenceEntry entry = (SequenceEntry) obj;

                return this.duration == entry.duration && Objects.equals(this.morph, entry.morph);
            }

            return super.equals(obj);
        }
    }
}