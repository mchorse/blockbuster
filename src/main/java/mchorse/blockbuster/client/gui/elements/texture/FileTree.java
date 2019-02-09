package mchorse.blockbuster.client.gui.elements.texture;

import java.util.Comparator;

import mchorse.blockbuster.client.gui.elements.texture.AbstractEntry.FolderEntry;

/**
 * File tree
 * 
 * The implementations of file tree are responsible for creating full 
 * tree of files, so that {@link GuiTexturePicker} could navigate it.
 */
public abstract class FileTree
{
    /**
     * Abstract entry sorter. Sorts folders on top first, and then by 
     * the display title name   
     */
    public static Comparator<AbstractEntry> SORTER = new EntrySorter();

    /**
     * Root entry, this top folder should be populated in 
     * {@link #rebuild()} method.
     */
    public FolderEntry root = new FolderEntry("root", null);

    /**
     * Does the tree needs a rebuild 
     */
    public boolean needsRebuild = true;

    /**
     * Rebuild the file tree 
     */
    public abstract void rebuild();

    /**
     * Adds a "back to parent directory" entry 
     */
    public void addBackEntry(FolderEntry entry)
    {
        FolderEntry top = new FolderEntry("../", entry);

        top.entries = entry.parent.entries;
        entry.entries.add(top);
    }

    /**
     * Get a top level folder for given name    
     */
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