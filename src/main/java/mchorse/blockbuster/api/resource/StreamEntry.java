package mchorse.blockbuster.api.resource;

import mchorse.blockbuster.Blockbuster;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StreamEntry implements IResourceEntry
{
    public String path;
    public long time;
    public ClassLoader loader = Blockbuster.class.getClassLoader();

    public StreamEntry(String path, long time)
    {
        this.path = path;
        this.time = time;
    }

    @Override
    public String getName()
    {
        return this.path == null ? "" : FilenameUtils.getName(this.path);
    }

    public StreamEntry(String path, long time, ClassLoader loader)
    {
        this(path, time);

        this.loader = loader;
    }

    @Override
    public InputStream getStream() throws IOException
    {
        return this.path == null ? null : this.loader.getResourceAsStream(this.path);
    }

    @Override
    public boolean exists()
    {
        return this.path != null && this.loader.getResource(this.path) != null;
    }

    @Override
    public long lastModified()
    {
        return this.time;
    }

    @Override
    public boolean copyTo(File file)
    {
        try
        {
            FileUtils.copyInputStreamToFile(this.getStream(), file);

            return true;
        }
        catch (IOException e)
        {}

        return false;
    }
}
