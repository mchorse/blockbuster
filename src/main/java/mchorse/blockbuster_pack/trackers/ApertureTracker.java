package mchorse.blockbuster_pack.trackers;

import mchorse.aperture.Aperture;
import mchorse.aperture.camera.CameraExporter;
import mchorse.aperture.camera.minema.MinemaIntegration;
import mchorse.aperture.client.gui.GuiMinemaPanel;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster_pack.morphs.TrackerMorph;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;

public class ApertureTracker extends BaseTracker
{
    private CameraExporter.TrackingPacket trackingPacket = null;

    public boolean combineTracking;

    @Override
    @Optional.Method(modid = Aperture.MOD_ID)
    public void track(EntityLivingBase target, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(!ReflectionUtils.isOptifineShadowPass() && !this.name.equals(""))
        {
            if(CameraHandler.isApertureAndMinemaLoaded())
            {
                if (MinemaIntegration.isRecording() && this.trackingPacket == null)
                {
                    CameraExporter.TrackingPacket packet = new CameraExporter.TrackingPacket(this.name, this.combineTracking);

                    if (GuiMinemaPanel.trackingExporter.addTracker(packet))
                    {
                        this.trackingPacket = packet;

                        //dont rename - when actors are present it can result in canMerge not being called before render
                        //this.name = packet.getName();
                    }
                }
                else if (!MinemaIntegration.isRecording() && this.trackingPacket != null)
                {
                    if(this.trackingPacket.isReset())
                    {
                        this.trackingPacket = null;
                    }
                }

                if (MinemaIntegration.isRecording() && this.trackingPacket != null)
                {
                    GuiMinemaPanel.trackingExporter.track(this.trackingPacket, target, partialTicks);
                }
            }
        }
    }

    @Override
    public boolean canMerge(BaseTracker tracker)
    {
        if (tracker != null && tracker instanceof ApertureTracker)
        {
            ApertureTracker apTracker = (ApertureTracker) tracker;
            this.combineTracking = apTracker.combineTracking;

            return super.canMerge(tracker);
        }

        return false;
    }

    @Override
    public void copy(BaseTracker tracker)
    {
        if (tracker != null && tracker instanceof ApertureTracker)
        {
            ApertureTracker trackerAperture = (ApertureTracker) tracker;

            this.combineTracking = trackerAperture.combineTracking;
        }

        super.copy(tracker);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj instanceof ApertureTracker)
        {
            ApertureTracker morph = (ApertureTracker) obj;

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
}
