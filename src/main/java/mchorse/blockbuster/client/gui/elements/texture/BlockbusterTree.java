package mchorse.blockbuster.client.gui.elements.texture;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import mchorse.blockbuster.utils.RLUtils;

public class BlockbusterTree extends FileTree
{
    public File folder;

    public BlockbusterTree(File folder)
    {
        this.folder = folder;
        this.update();
    }

    @Override
    public void update()
    {
        this.root.entries.clear();

        for (File file : this.folder.listFiles())
        {
            if (file.isDirectory())
            {
                FolderEntry entry = new FolderEntry(file.getName(), this.root);
                File skins = new File(file, "skins");

                if (skins.isDirectory())
                {
                    this.addEntries(skins, entry, file.getName() + "/skins");
                }

                this.root.entries.add(entry);
            }
        }
    }

    public void addEntries(File skins, FolderEntry entry, String prefix)
    {
        FolderEntry top = new FolderEntry("../", entry);

        top.entries = entry.parent.entries;
        entry.entries.add(top);

        for (File skin : skins.listFiles())
        {
            AbstractEntry skinEntry = null;
            String name = skin.getName();

            if (skin.exists())
            {
                if (skin.isDirectory())
                {
                    skinEntry = new FolderEntry(name, entry);
                    this.addEntries(skin, (FolderEntry) skinEntry, prefix + "/" + name);
                }
                else if (skin.isFile())
                {
                    if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif"))
                    {
                        skinEntry = new FileEntry(name, RLUtils.create("b.a", prefix + "/" + name));
                    }
                }
            }

            if (skinEntry != null)
            {
                entry.entries.add(skinEntry);
            }
        }

        Collections.sort(entry.entries, new Comparator<AbstractEntry>()
        {
            @Override
            public int compare(AbstractEntry o1, AbstractEntry o2)
            {
                if (o1 instanceof FolderEntry && !(o2 instanceof FolderEntry))
                {
                    return -1;
                }

                return o1.title.compareToIgnoreCase(o2.title);
            }
        });
    }

    public FolderEntry getEntryForName(String name)
    {
        for (AbstractEntry entry : this.root.entries)
        {
            if (entry instanceof FolderEntry)
            {
                FolderEntry folder = (FolderEntry) entry;

                if (folder.title.equalsIgnoreCase(name))
                {
                    return folder;
                }
            }
        }

        return this.root;
    }
}