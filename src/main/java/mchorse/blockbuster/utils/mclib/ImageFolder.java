package mchorse.blockbuster.utils.mclib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageFolder extends File
{
    private static final long serialVersionUID = 2087807134801481836L;

    public ImageFolder(String pathname)
    {
        super(pathname);
    }

    public ImageFolder(File parent, String child)
    {
        super(parent, child);
    }

    @Override
    public File[] listFiles()
    {
        return this.processFiles(super.listFiles());
    }

    private File[] processFiles(File[] files)
    {
        List<File> list = new ArrayList<File>();

        for (File file : files)
        {
            if (file.isFile())
            {
                if (file.getName().toLowerCase().endsWith(".gif"))
                {
                    File gif = new GifFolder(file.getPath());

                    if (gif.exists())
                    {
                        list.add(gif);
                    }
                }

                list.add(file);
            }
            else if (file.isDirectory())
            {
                list.add(new ImageFolder(file.getPath()));
            }
        }

        return list.toArray(new File[0]);
    }
}
