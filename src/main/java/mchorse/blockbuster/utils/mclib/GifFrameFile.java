package mchorse.blockbuster.utils.mclib;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GifFrameFile extends File
{
    private static final long serialVersionUID = -3183927604124452726L;
    private static final Pattern match = Pattern.compile("\\.gif>\\/frame(\\d+)\\.png$");

    public GifFolder parent;
    public int index;

    public GifFrameFile(String pathname)
    {
        super(pathname.substring(0, pathname.indexOf(".gif>/") + 4));

        this.init(pathname);
    }

    public GifFrameFile(File parent, String child)
    {
        super(parent, child.substring(0, child.indexOf(".gif>/") + 4));

        this.init(child);
    }

    private void init(String pathname)
    {
        this.parent = new GifFolder(super.getPath());
        this.index = -1;

        if (this.parent.exists())
        {
            Matcher matcher = match.matcher(pathname);

            if (matcher.find())
            {
                int index = Integer.parseInt(matcher.group(1));

                if (index < this.parent.gif.getFrameCount())
                {
                    this.index = index;
                }
            }
        }
    }

    @Override
    public String getName()
    {
        return "frame" + this.index + ".png";
    }

    @Override
    public String getParent()
    {
        return super.getPath();
    }

    @Override
    public File getParentFile()
    {
        return this.parent;
    }

    @Override
    public boolean isDirectory()
    {
        return false;
    }

    @Override
    public boolean isFile()
    {
        return true;
    }

    @Override
    public boolean exists()
    {
        return this.index != -1;
    }
}
