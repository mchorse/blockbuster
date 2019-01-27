package mchorse.blockbuster.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

/**
 * Multiple resource location class
 * 
 * This bad boy allows constructing a single texture out of several
 */
public class MultiResourceLocation extends ResourceLocation
{
    public List<ResourceLocation> children = new ArrayList<ResourceLocation>();

    public MultiResourceLocation(String resourceName)
    {
        super(resourceName);
    }

    public MultiResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        super(resourceDomainIn, resourcePathIn);
    }
}