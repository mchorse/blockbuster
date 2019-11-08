package mchorse.blockbuster.utils;

import java.io.File;
import java.util.Collections;

import mchorse.mclib.utils.files.AbstractEntry;
import mchorse.mclib.utils.files.AbstractEntry.FileEntry;
import mchorse.mclib.utils.files.AbstractEntry.FolderEntry;
import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.resources.RLUtils;

/**
 * Blockbuster custom model system's file tree
 * 
 * This bad boy looks through the models' skins folder and recursively 
 * collects all the stuff.  
 */
public class BlockbusterTree extends FileTree
{
    /**
     * Folder in which the tree must look for the files
     */
    public File folder;

    public BlockbusterTree(File folder)
    {
        this.folder = folder;
        this.root.title = "b.a";
        this.root.file = folder;
    }

    @Override
    public void rebuild()
    {
        if (!this.needsRebuild)
        {
            return;
        }

        this.root.entries.clear();

        for (File file : this.folder.listFiles())
        {
            if (file.isDirectory())
            {
                FolderEntry entry = new FolderEntry(file.getName(), this.root, file);

                if (file.isDirectory())
                {
                    this.addEntries(file, entry, file.getName());
                }

                /* Skip empty folder */
                if (!entry.entries.isEmpty())
                {
                    this.root.entries.add(entry);
                }
            }
        }

        this.needsRebuild = false;
    }

    /**
     * Add recursively entries to given folder entry 
     */
    public void addEntries(File skins, FolderEntry entry, String prefix)
    {
        for (File skin : skins.listFiles())
        {
            AbstractEntry skinEntry = null;
            String name = skin.getName();

            if (skin.exists())
            {
                if (skin.isDirectory())
                {
                    FolderEntry folder = new FolderEntry(name, entry, skin);
                    this.addEntries(skin, folder, prefix + "/" + name);

                    if (folder.entries.isEmpty())
                    {
                        continue;
                    }

                    skinEntry = folder;
                }
                else if (skin.isFile())
                {
                    /* Only textures files should be shown */
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

        /* Don't add anything to empty folder */
        if (entry.entries.isEmpty())
        {
            return;
        }

        this.addBackEntry(entry);

        Collections.sort(entry.entries, FileTree.SORTER);
    }
}