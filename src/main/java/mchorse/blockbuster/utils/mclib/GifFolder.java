package mchorse.blockbuster.utils.mclib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;

public class GifFolder extends File
{
    public static Map<String, Long> lastModified = new HashMap<String, Long>();
    public static Map<String, GifImage> cache = new HashMap<String, GifImage>();

    private static final long serialVersionUID = 3058345951609134509L;

    public GifImage gif;

    public GifFolder(String pathname)
    {
        super(pathname);

        String path = this.getPath();
        Long last = lastModified.get(path);

        if (last != null && last <= this.lastModified())
        {
            this.gif = cache.get(path);
        }
        else
        {
            try
            {
                InputStream in = new FileInputStream(pathname);

                this.gif = GifDecoder.read(in);

                in.close();
                cache.put(path, this.gif);
                lastModified.put(path, this.lastModified());
            }
            catch (IOException e)
            {
                this.gif = null;
                cache.remove(path);
                lastModified.remove(path);
            }
        }
    }

    @Override
    public String getName()
    {
        return super.getName() + ">";
    }

    @Override
    public String getPath()
    {
        return super.getPath() + ">";
    }

    @Override
    public boolean isDirectory()
    {
        return true;
    }

    @Override
    public boolean isFile()
    {
        return false;
    }

    @Override
    public File[] listFiles()
    {
        List<File> list = new ArrayList<File>();

        for (int i = 0; i < this.gif.getFrameCount(); i++)
        {
            list.add(new GifFrameFile(this.getPath() + "/frame" + i + ".png"));
        }

        return list.toArray(new File[0]);
    }

    @Override
    public boolean exists()
    {
        return this.gif != null;
    }
}
