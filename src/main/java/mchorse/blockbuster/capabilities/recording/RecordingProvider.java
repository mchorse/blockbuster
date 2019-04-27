package mchorse.blockbuster.capabilities.recording;

import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Recording provider
 *
 * Basic version of a capability provider. Most of the code is taken from
 * {@link MorphingProvider}.
 */
public class RecordingProvider implements ICapabilityProvider
{
    @CapabilityInject(IRecording.class)
    public static final Capability<IRecording> RECORDING = null;

    private IRecording instance = RECORDING.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == RECORDING;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == RECORDING ? RECORDING.<T>cast(this.instance) : null;
    }
}