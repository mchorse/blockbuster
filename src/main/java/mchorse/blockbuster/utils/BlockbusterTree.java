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
    public BlockbusterTree(File folder)
    {
        this.root = new FolderImageEntry("b.a", folder, null);
    }
}