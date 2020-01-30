package mchorse.blockbuster.api.resource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileEntry implements IResourceEntry
{
    public File file;

    public FileEntry(File file)
    {
        this.file = file;
    }

    @Override
    public String getName()
    {
        return this.file == null ? "" : this.file.getName();
    }

    @Override
    public InputStream getStream() throws IOException
    {
        return this.file == null ? null : new FileInputStream(this.file);
    }

    @Override
    public boolean exists()
    {
        return this.file == null ? false : this.file.exists();
    }

    @Override
    public long lastModified()
    {
        return this.file == null ? 0 : this.file.lastModified();
    }

    @Override
    public void copyTo(File file) throws IOException
    {
        FileUtils.copyFile(this.file, file);
    }
}
