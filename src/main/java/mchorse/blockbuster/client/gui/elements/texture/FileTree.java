package mchorse.blockbuster.client.gui.elements.texture;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public class FileTree
{
    public FolderEntry root = new FolderEntry("root", null);

    public void update()
    {}

    public static abstract class AbstractEntry
    {
        public String title = "";

        public AbstractEntry(String title)
        {
            this.title = title;
        }

        public boolean isFolder()
        {
            return this instanceof FolderEntry;
        }
    }

    public static class FolderEntry extends AbstractEntry
    {
        public FolderEntry parent;
        public List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

        public FolderEntry(String title, FolderEntry parent)
        {
            super(title);

            this.parent = parent;
        }
    }

    public static class FileEntry extends AbstractEntry
    {
        public ResourceLocation resource;

        public FileEntry(String title, ResourceLocation resource)
        {
            super(title);

            this.resource = resource;
        }
    }
}