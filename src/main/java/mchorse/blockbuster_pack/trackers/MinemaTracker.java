package mchorse.blockbuster_pack.trackers;

import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.MinemaAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.Optional.Method;

public class MinemaTracker extends BaseTracker
{
    private boolean available;

    @Override
    @Method(modid = Minema.MODID)
    public void init()
    {
        this.available = false;
        try
        {
            Class.forName("info.ata4.minecraft.minema.MinemaAPI").getMethod("doTrack", String.class);
            this.available = true;
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException e)
        {}
    }

    @Override
    @Method(modid = Minema.MODID)
    public void track(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (available)
        {
            MinemaAPI.doTrack(name);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof MinemaTracker && super.equals(obj);
    }
}
