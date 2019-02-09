package mchorse.blockbuster.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

import net.minecraft.util.ResourceLocation;

/**
 * Multiple resource location class
 * 
 * This bad boy allows constructing a single texture out of several 
 * {@link ResourceLocation}s. It doesn't really make sense for other 
 * types of resources beside pictures.
 */
public class MultiResourceLocation extends ResourceLocation
{
    public List<ResourceLocation> children = new ArrayList<ResourceLocation>();

    public MultiResourceLocation(String resourceName)
    {
        /* This needed so there would less chances to match with an 
         * actual ResourceLocation */
        super("it_would_be_very_ironic:if_this_would_match_with_regular_rls");
        this.children.add(RLUtils.create(resourceName));
    }

    public MultiResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        super("it_would_be_very_ironic", "if_this_would_match_with_regular_rls");
        this.children.add(RLUtils.create(resourceDomainIn, resourcePathIn));
    }

    @Override
    public String getResourceDomain()
    {
        return this.children.isEmpty() ? "" : this.children.get(0).getResourceDomain();
    }

    @Override
    public String getResourcePath()
    {
        return this.children.isEmpty() ? "" : this.children.get(0).getResourcePath();
    }

    /**
     * This is mostly for looks, but it doesn't really makes sense by  
     * itself
     */
    @Override
    public String toString()
    {
        return this.getResourceDomain() + ":" + this.getResourcePath();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) obj;

            boolean same = this.children.size() == multi.children.size();

            if (same)
            {
                for (int i = 0, c = this.children.size(); i < c; i++)
                {
                    same = same && Objects.equal(this.children.get(i), multi.children.get(i));
                }
            }

            return same;
        }

        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        int hash = super.hashCode();

        for (int i = 0, c = this.children.size(); i < c; i++)
        {
            hash = 31 * hash + this.children.hashCode();
        }

        return hash;
    }
}