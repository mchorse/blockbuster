package mchorse.blockbuster.utils;

import java.io.File;
import java.util.Collections;

import mchorse.mclib.utils.files.AbstractEntry;
import mchorse.mclib.utils.files.AbstractEntry.FileEntry;
import mchorse.mclib.utils.files.AbstractEntry.FolderEntry;
import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.files.entries.FolderImageEntry;
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
        this.root = new FolderImageEntry("b.a", folder, null);
    }
}