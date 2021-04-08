package mchorse.blockbuster.utils.mclib;

import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

/**
 * Blockbuster jar file tree
 */
public class BlockbusterJarTree extends FileTree
{
    public BlockbusterJarTree()
    {
        this.root = new FolderEntry("blockbuster", null, null);

        FolderEntry textures = new FolderEntry("textures", null, this.root);
        FolderEntry entity = new FolderEntry("entity", null, textures);
        FolderEntry eyeMasks = new FolderEntry("eye_masks", null, entity);
        FolderEntry eyes = new FolderEntry("eyes", null, entity);

        FileEntry mask1 = new FileEntry("1px.png", null, new ResourceLocation("blockbuster:textures/entity/eye_masks/1px.png"));
        FileEntry mask2 = new FileEntry("1px_l.2px_r.png", null, new ResourceLocation("blockbuster:textures/entity/eye_masks/1px_l.2px_r.png"));
        FileEntry mask3 = new FileEntry("2px.png", null, new ResourceLocation("blockbuster:textures/entity/eye_masks/2px.png"));
        FileEntry mask4 = new FileEntry("2px_l.1px_r.png", null, new ResourceLocation("blockbuster:textures/entity/eye_masks/2px_l.1px_r.png"));

        FileEntry alex = new FileEntry("alex", null, new ResourceLocation("blockbuster:textures/entity/eyes/alex.png"));
        FileEntry steve = new FileEntry("steve", null, new ResourceLocation("blockbuster:textures/entity/eyes/steve.png"));

        eyeMasks.getRawEntries().add(mask1);
        eyeMasks.getRawEntries().add(mask2);
        eyeMasks.getRawEntries().add(mask3);
        eyeMasks.getRawEntries().add(mask4);

        eyes.getRawEntries().add(alex);
        eyes.getRawEntries().add(steve);

        this.root.getRawEntries().add(textures);
        textures.getRawEntries().add(entity);
        entity.getRawEntries().add(eyeMasks);
        entity.getRawEntries().add(eyes);

        this.addBackEntryTo(this.root);
        this.addBackEntryTo(textures);
        this.addBackEntryTo(entity);
        this.addBackEntryTo(eyeMasks);
        this.addBackEntryTo(eyes);

        System.out.println("");
    }

    private void addBackEntryTo(FolderEntry entry)
    {
        Collections.sort(entry.getRawEntries(), FileTree.SORTER);
        FileTree.addBackEntry(entry);
    }
}