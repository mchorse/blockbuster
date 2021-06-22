package mchorse.blockbuster.utils.mclib;

import mchorse.blockbuster.Blockbuster;
import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.util.Collections;

/**
 * Blockbuster jar file tree
 */
public class BlockbusterJarTree extends FileTree
{
    public BlockbusterJarTree()
    {
        this.root = new FolderEntry("blockbuster", null, null);

        this.add("textures/entity/eye_masks/1px.png");
        this.add("textures/entity/eye_masks/1px_l.2px_r.png");
        this.add("textures/entity/eye_masks/2px.png");
        this.add("textures/entity/eye_masks/2px_l.1px_r.png");
        this.add("textures/entity/eyes/steve.png");
        this.add("textures/entity/eyes/alex.png");
        this.add("textures/entity/mchorse/eyes.png");
        this.add("textures/entity/mchorse/head.png");
        this.add("textures/entity/mchorse/skin.png");
        this.add("textures/entity/actor.png");
        this.add("textures/entity/cape.png");
        this.add("textures/gui/icon.png");
    }

    protected void add(String path)
    {
        String[] splits = path.split("/");
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

        ResourceLocation location = new ResourceLocation(Blockbuster.MOD_ID, path);
        FileEntry file = new FileEntry(splits[splits.length - 1] , null, location);

        entry.getRawEntries().add(file);
    }

    private void addBackEntryTo(FolderEntry entry)
    {
        Collections.sort(entry.getRawEntries(), FileTree.SORTER);
        FileTree.addBackEntry(entry);
    }
}