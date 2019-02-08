package mchorse.blockbuster.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

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
        super("it_would_be_very_ironic:if_this_would_work");
        this.children.add(RLUtils.create(resourceName));
    }

    public MultiResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        super("it_would_be_very_ironic", "if_this_would_work");
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
        int c = this.children.size();

        if (c == 0)
        {
            return "";
        }

        String str = this.children.get(0).getResourcePath();

        if (c > 1)
        {
            str += "[";

            for (int i = 1; i < c; i++)
            {
                str += this.children.get(i);
            }

            str += "]";
        }

        return str;
    }

    @Override
    public String toString()
    {
        return super.toString();
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