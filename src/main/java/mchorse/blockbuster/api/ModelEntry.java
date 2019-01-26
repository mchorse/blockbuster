package mchorse.blockbuster.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser;

/**
 * Model entry 
 */
public class ModelEntry
{
    public IResourceEntry customModel;
    public IResourceEntry objModel;
    public IResourceEntry mtlFile;

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

    public ModelEntry(Object customModel, Object objModel, Object mtlFile)
    {
        this.customModel = createEntry(customModel);
        this.objModel = createEntry(objModel);
        this.mtlFile = createEntry(mtlFile);
    }

    public long lastModified()
    {
        long a = this.customModel.lastModified();
        long b = this.objModel.lastModified();
        long c = this.mtlFile.lastModified();

        return Math.max(Math.max(a, b), c);
    }

    public OBJParser createOBJParser(String key, Model model)
    {
        if (!model.providesObj)
        {
            return null;
        }

        OBJParser parser = null;

        try
        {
            InputStream obj = this.objModel.getStream();
            InputStream mtl = model.providesMtl ? this.mtlFile.getStream() : null;

            parser = new OBJParser(obj, mtl);
            parser.read();

            if (this.objModel instanceof FileEntry)
            {
                parser.setupTextures(key, ((FileEntry) this.objModel).file.getParentFile());
            }

            model.materials.putAll(parser.materials);
        }
        catch (Exception e)
        {
            return null;
        }

        return parser;
    }

    public static interface IResourceEntry
    {
        public InputStream getStream() throws IOException;

        public boolean exists();

        public long lastModified();

        public void copyTo(File file) throws IOException;
    }

    public static class FileEntry implements IResourceEntry
    {
        public File file;

        public FileEntry(File file)
        {
            this.file = file;
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

    public static class StreamEntry implements IResourceEntry
    {
        public String path;
        public long time;

        public StreamEntry(String path, long time)
        {
            this.path = path;
            this.time = time;
        }

        @Override
        public InputStream getStream() throws IOException
        {
            return this.path == null ? null : Blockbuster.class.getResourceAsStream(this.path);
        }

        @Override
        public boolean exists()
        {
            return this.path == null ? false : Blockbuster.class.getResource(this.path) != null;
        }

        @Override
        public long lastModified()
        {
            return this.time;
        }

        @Override
        public void copyTo(File file) throws IOException
        {
            FileUtils.copyInputStreamToFile(this.getStream(), file);
        }
    }
}