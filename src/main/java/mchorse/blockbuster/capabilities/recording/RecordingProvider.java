package mchorse.blockbuster.capabilities.recording;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Recording provider
 *
 * Basic version of a capability provider. Most of the code is taken from
 * {@link MorphingProvider}.
 */
public class RecordingProvider implements ICapabilitySerializable<NBTBase>
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
        return capability == RECORDING ? RECORDING.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return RECORDING.getStorage().writeNBT(RECORDING, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        RECORDING.getStorage().readNBT(RECORDING, this.instance, null, nbt);
    }
}