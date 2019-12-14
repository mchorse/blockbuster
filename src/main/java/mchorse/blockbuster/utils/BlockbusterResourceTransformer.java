package mchorse.blockbuster.utils;

import mchorse.mclib.utils.resources.IResourceTransformer;
import org.apache.commons.lang3.StringUtils;

import java.util.StringJoiner;

public class BlockbusterResourceTransformer implements IResourceTransformer
{
    public static final String DOMAIN = "b.a";
    public static final String OLD_DOMAIN = "blockbuster.actors";

    @Override
    public String transformDomain(String domain, String path)
    {
        if (domain.equals(OLD_DOMAIN))
        {
            domain = DOMAIN;
        }

        return domain;
    }

    @Override
    public String transformPath(String domain, String path)
    {
        /* Fix old fashion model/skin resource locations */
        if (domain.equals(DOMAIN) || domain.equals(OLD_DOMAIN))
        {
            path = this.fixPath(path);
        }

        return path;
    }

    @Override
    public String transform(String location)
    {
        if (location.startsWith(OLD_DOMAIN + ":"))
        {
            location = DOMAIN + location.substring(OLD_DOMAIN.length());
        }

        if (location.startsWith(DOMAIN + ":") && StringUtils.countMatches(location, "/") == 1 && location.indexOf(".") == -1)
        {
            int index = location.indexOf(":");

            String domain = location.substring(0, index + 1);
            String path = this.fixPath(location.substring(index + 1));

            location = domain + path;
        }

        return location;
    }

    private String fixPath(String path)
    {
        if (path.indexOf(".") != -1)
        {
            return path;
        }

        String[] splits = path.split("/");

        if (splits.length != 2)
        {
            return path;
        }

        return splits[0] + "/skins/" + splits[1] + ".png";
    }
}