package mchorse.blockbuster.api.formats.vox.data;

import mchorse.blockbuster.api.formats.vox.VoxReader;

import java.io.InputStream;

public class VoxGroup extends VoxBaseNode
{
    public int[] ids;

    public VoxGroup(InputStream stream, VoxReader reader) throws Exception 
    {
        this.id = reader.readInt(stream);
        this.attrs = reader.readDictionary(stream);
        this.num = reader.readInt(stream);
        this.ids = new int[this.num];

        for (int i = 0; i < num; i ++)
        {
            this.ids[i] = reader.readInt(stream);
        }
    }
}