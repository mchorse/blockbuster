package mchorse.blockbuster_pack.trackers;

import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.MinemaAPI;
import mchorse.aperture.Aperture;
import mchorse.aperture.camera.CameraExporter;
import mchorse.aperture.client.gui.GuiMinemaPanel;
import mchorse.mclib.utils.ReflectionUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

public class MorphTracker extends BaseTracker
{
    private CameraExporter.TrackingPacket trackingPacket = null;
    /* for aperture tracking */
    private boolean combineTracking;
    private ApertureTracker apertureTracker;
    private MinemaTracker minemaTracker;

    public void setCombineTracking(boolean combineTracking)
    {
        this.combineTracking = combineTracking;
    }

    public boolean getCombineTracking()
    {
        return this.combineTracking;
    }

    @Override
    public void init()
    {
        if (Loader.isModLoaded(Minema.MODID)) {
            this.minemaTracker = new MinemaTracker();
        }

        if (Loader.isModLoaded(Aperture.MOD_ID)) {
            this.apertureTracker = new ApertureTracker();
        }
    }

    @Override
    public void track(EntityLivingBase target, double x, double y, double z, float entityYaw, float partialTicks)
    {
        /* minema tracking */
        if (this.minemaTracker != null) this.minemaTracker.track(this);

        /* aperture tracking */
        if (this.trackingPacket != null && this.trackingPacket.isReset())
        {
            this.trackingPacket = null;

            return;
        }

        if(this.apertureTracker != null && !ReflectionUtils.isOptifineShadowPass() && !this.name.equals(""))
        {
            this.apertureTracker.track(this);
        }
    }

    @Override
    public boolean canMerge(BaseTracker tracker)
    {
        if (tracker instanceof MorphTracker)
        {
            MorphTracker apTracker = (MorphTracker) tracker;
            this.combineTracking = apTracker.combineTracking;

            return super.canMerge(tracker);
        }

        return false;
    }

    @Override
    public void copy(BaseTracker tracker)
    {
        if (tracker instanceof MorphTracker)
        {
            MorphTracker trackerAperture = (MorphTracker) tracker;

            this.combineTracking = trackerAperture.combineTracking;
        }

        super.copy(tracker);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj instanceof MorphTracker)
        {
            MorphTracker morph = (MorphTracker) obj;

            result = super.equals(obj) && this.combineTracking == morph.combineTracking;
        }

        return result;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.combineTracking = tag.getBoolean("CombineTracking");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setBoolean("CombineTracking", this.combineTracking);

        return tag;
    }

    private static class Tracker {
        public void track(MorphTracker tracker) {}
    }

    private static class MinemaTracker extends Tracker
    {
        @Override
        @Optional.Method(modid = Minema.MODID)
        public void track(MorphTracker tracker)
        {
            MinemaAPI.doTrack(tracker.name);
        }
    }

    private static class ApertureTracker extends Tracker
    {
        @Override
        @Optional.Method(modid = Aperture.MOD_ID)
        public void track(MorphTracker tracker)
        {
            if (GuiMinemaPanel.trackingExporter.isTracking())
            {
                if (tracker.trackingPacket == null)
                {
                    CameraExporter.TrackingPacket packet = new CameraExporter.TrackingPacket(tracker.name, tracker.combineTracking);

                    if (GuiMinemaPanel.trackingExporter.addTracker(packet))
                    {
                        tracker.trackingPacket = packet;
                    }
                }

                GuiMinemaPanel.trackingExporter.track(tracker.trackingPacket);
            }
        }
    }
}
