package mchorse.blockbuster_pack.trackers;

import java.util.Objects;

import mchorse.mclib.network.INBTSerializable;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BaseTracker implements INBTSerializable
{
    public String name = "";

    public BaseTracker()
    {
        this.init();
    }

    public void init()
    {}

    public void track(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {}

    public BaseTracker copy()
    {
        BaseTracker tracker = null;

        try
        {
            tracker = this.getClass().newInstance();
            tracker.copy(this);
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return tracker;
    }

    public void copy(BaseTracker tracker)
    {
        if (tracker != null)
        {
            this.name = tracker.name;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BaseTracker)
        {
            return Objects.equals(this.name, ((BaseTracker) obj).name);
        }

        return super.equals(obj);
    }

    public boolean canMerge(BaseTracker morph)
    {
        if (morph != null)
        {
            return this.name.equals(morph);
        }

        return false;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.name = tag.getString("Name");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        tag.setString("Name", this.name);

        return tag;
    }
}
