package mchorse.blockbuster.utils;

import java.io.File;

import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.files.entries.FolderImageEntry;

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