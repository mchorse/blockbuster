package mchorse.blockbuster.utils.mclib;

import mchorse.blockbuster.utils.ResourcePackUtils;
import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Comparator;
import java.util.List;

/**
 * Blockbuster jar file tree
 */
public class BlockbusterJarTree extends FileTree
{
    public BlockbusterJarTree()
    {
        this.root = new FolderEntry("blockbuster", null, null);

        List<ResourceLocation> allPictures = ResourcePackUtils.getAllPictures(Minecraft.getMinecraft().getResourceManager());

        allPictures.sort(Comparator.comparing(ResourceLocation::getResourcePath));

        for (ResourceLocation location : allPictures)
        {
            this.add(location);
        }

        this.recursiveSort(this.root);
    }

    private void recursiveSort(FolderEntry folder)
    {
        folder.getRawEntries().sort(FileTree.SORTER);

        for (AbstractEntry entry : folder.getRawEntries())
        {
            if (entry instanceof FolderEntry)
            {
                this.recursiveSort((FolderEntry) entry);
            }
        }
    }

    protected void add(ResourceLocation location)
    {
        String[] splits = location.getResourcePath().split("/");
        FolderEntry entry = this.root;

        main:
        for (int i = 0; i < splits.length - 1; i++)
        {
            for (AbstractEntry entryChild : entry.getRawEntries())
            {
                if (entry.isFolder() && entryChild.title.equals(splits[i]))
                {
                    entry = (FolderEntry) entryChild;

                    continue main;
                }
            }

            FolderEntry folder = new FolderEntry(splits[i], null, entry);

            this.addBackEntryTo(folder);
            entry.getRawEntries().add(folder);
            entry = folder;
        }

        FileEntry file = new FileEntry(splits[splits.length - 1] , null, location);

        entry.getRawEntries().add(file);
    }

    private void addBackEntryTo(FolderEntry entry)
    {
        entry.getRawEntries().sort(FileTree.SORTER);

        FileTree.addBackEntry(entry);
    }
}