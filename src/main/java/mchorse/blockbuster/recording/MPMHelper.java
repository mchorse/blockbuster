package mchorse.blockbuster.recording;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;

/**
 * MPM helper class
 * 
 * This class is responsible for setting and getting current MPM model data for 
 * given player. Reden (Charles) helped me to figure out this stuff.
 */
public class MPMHelper
{
    /* Reflection fields */
    public static Method get;
    public static Method set;
    public static Method getThing;

    /**
     * Checks whether MPM mod is loaded
     */
    public static boolean isLoaded()
    {
        return Loader.isModLoaded("moreplayermodels");
    }

    /**
     * Initiate method fields for later usage with get and set MPM data methods
     */
    public static void init()
    {
        try
        {
            Class clazz = Class.forName("noppes.mpm.ModelData");

            get = clazz.getMethod("writeToNBT");
            set = clazz.getMethod("readFromNBT", NBTTagCompound.class);
            getThing = clazz.getMethod("get", EntityPlayer.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets MPM data from given player
     */
    public static NBTTagCompound getMPMData(EntityPlayer entity)
    {
        if (get == null)
        {
            init();
        }

        if (get != null)
        {
            try
            {
                return (NBTTagCompound) get.invoke(getThing.invoke(null, entity));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Sets MPM data on a given player
     */
    public static void setMPMData(EntityPlayer entity, NBTTagCompound tag)
    {
        if (set != null)
        {
            try
            {
                set.invoke(getThing.invoke(null, entity), tag);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}