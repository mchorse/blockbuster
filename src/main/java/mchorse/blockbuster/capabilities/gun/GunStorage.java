package mchorse.blockbuster.capabilities.gun;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class GunStorage implements IStorage<IGun>
{
    @Override
    public NBTBase writeNBT(Capability<IGun> capability, IGun instance, EnumFacing side)
    {
        return instance.getProps().toNBT();
    }

    @Override
    public void readNBT(Capability<IGun> capability, IGun instance, EnumFacing side, NBTBase nbt)
    {
        if (nbt instanceof NBTTagCompound)
        {
            instance.getProps().fromNBT((NBTTagCompound) nbt);
        }
    }
}