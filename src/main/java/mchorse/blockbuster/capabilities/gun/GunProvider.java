package mchorse.blockbuster.capabilities.gun;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class GunProvider implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IGun.class)
    public static final Capability<IGun> GUN = null;

    private IGun instance = GUN.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == GUN;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == GUN ? GUN.<T>cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return GUN.getStorage().writeNBT(GUN, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        GUN.getStorage().readNBT(GUN, this.instance, null, nbt);
    }
}