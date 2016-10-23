package mchorse.blockbuster.capabilities.morphing;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Morhping capability provider
 *
 * Now that I understand capability system, it seems pretty easy to use!
 */
public class MorphingProvider implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IMorphing.class)
    public static final Capability<IMorphing> MORPHING = null;

    private IMorphing instance = MORPHING.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == MORPHING;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == MORPHING ? MORPHING.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return MORPHING.getStorage().writeNBT(MORPHING, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        MORPHING.getStorage().readNBT(MORPHING, this.instance, null, nbt);
    }
}