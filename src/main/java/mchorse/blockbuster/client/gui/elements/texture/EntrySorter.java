package mchorse.blockbuster.client.gui.elements.texture;

import java.util.Comparator;

import mchorse.blockbuster.client.gui.elements.texture.AbstractEntry.FileEntry;
import mchorse.blockbuster.client.gui.elements.texture.AbstractEntry.FolderEntry;

/**
 * Abstract entry sorter
 * 
 * Sorts by folder first, and then by display name (title)
 */
public class EntrySorter implements Comparator<AbstractEntry>
{
    @Override
    public int compare(AbstractEntry o1, AbstractEntry o2)
    {
        if (o1 instanceof FolderEntry && o2 instanceof FileEntry)
        {
            return -1;
        }
        else if (o1 instanceof FileEntry && o2 instanceof FolderEntry)
        {
            return 1;
        }

        return o1.title.compareToIgnoreCase(o2.title);
    }
}