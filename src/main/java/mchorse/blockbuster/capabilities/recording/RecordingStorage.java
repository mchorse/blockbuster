package mchorse.blockbuster.capabilities.recording;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Recording capability storage
 *
 * This storage saves only current camera profile's name, because the other
 * fields like the timestamp of camera profile or recording information does
 * needed only during runtime.
 *
 * Basically, when client joins, he doesn't have any of that information except
 * latest camera profile he had in previous session, so we just don't store it.
 */
public class RecordingStorage implements IStorage<IRecording>
{
    @Override
    public NBTBase writeNBT(Capability<IRecording> capability, IRecording instance, EnumFacing side)
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("Profile", instance.currentProfile());

        return tag;
    }

    @Override
    public void readNBT(Capability<IRecording> capability, IRecording instance, EnumFacing side, NBTBase nbt)
    {
        if (nbt instanceof NBTTagCompound)
        {
            NBTTagCompound tag = (NBTTagCompound) nbt;

            instance.setCurrentProfile(tag.getString("Profile"));
        }
    }
}