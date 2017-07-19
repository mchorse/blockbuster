package mchorse.blockbuster.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.util.ResourceLocation;

/**
 * Texture location
 *
 * A hack class that allows to use uppercase characters in the path 1.11.2 and
 * up.
 */
public class TextureLocation extends ResourceLocation
{
    public TextureLocation(String domain, String path)
    {
        super(domain, path);

        this.set(domain, path);
    }

    public TextureLocation(String string)
    {
        super(string);

        String[] split = string.split(":");
        String domain = split.length > 0 ? split[0] : "blockbuster.actors";
        String path = split.length > 1 ? split[1] : "";

        this.set(domain, path);
    }

    protected void set(String domain, String path)
    {
        /* Guess what it does */
        Field[] fields = ResourceLocation.class.getDeclaredFields();

        for (Field field : fields)
        {
            try
            {
                this.unlockField(field);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            fields[0].set(this, domain);
            fields[1].set(this, path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void unlockField(Field field) throws Exception
    {
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }
}