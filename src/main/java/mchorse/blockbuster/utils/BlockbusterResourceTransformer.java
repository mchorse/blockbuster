package mchorse.blockbuster.utils;

import mchorse.mclib.utils.resources.IResourceTransformer;

public class BlockbusterResourceTransformer implements IResourceTransformer
{
    @Override
    public String transformDomain(String domain)
    {
        if (domain.equals("blockbuster.actors"))
        {
            domain = "b.a";
        }

        return domain;
    }

    @Override
    public String transformPath(String path)
    {
        return path;
    }

    @Override
    public String transform(String location)
    {
        if (location.startsWith("blockbuster.actors:"))
        {
            location = "b.a" + location.substring(18);
        }

        return location;
    }
}