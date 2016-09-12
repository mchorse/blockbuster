package mchorse.blockbuster.actor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Morhping capability provider
 *
 * I don't know what I did here, I just copied some code from NoBedSleepingTest
 * mod... Capabilities are haaard!
 */
public class MorphingProvider implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IMorphing.class)
    public static final Capability<IMorphing> MORPHING_CAP = null;

    private IMorphing instance;

    public MorphingProvider()
    {
        this.instance = MORPHING_CAP.getDefaultInstance();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == MORPHING_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == MORPHING_CAP ? MORPHING_CAP.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return MORPHING_CAP.getStorage().writeNBT(MORPHING_CAP, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        MORPHING_CAP.getStorage().readNBT(MORPHING_CAP, this.instance, null, nbt);
    }
}