package mchorse.blockbuster.api.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Resource entry
 *
 * Used for the model system to allow both file based stream creation
 * and just stream creation via class loader/inside of jar
 */
public interface IResourceEntry
{
    public static IResourceEntry createEntry(Object object)
    {
        if (object instanceof File)
        {
            return new FileEntry((File) object);
        }
        else if (object instanceof String)
        {
            return new StreamEntry((String) object, System.currentTimeMillis());
        }

        return new StreamEntry(null, 0);
    }

    public InputStream getStream() throws IOException;

    public boolean exists();

    public long lastModified();

    public void copyTo(File file) throws IOException;
}
