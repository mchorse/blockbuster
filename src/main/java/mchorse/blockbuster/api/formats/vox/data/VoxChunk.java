package mchorse.blockbuster.api.formats.vox.data;

/**
 * This represents a data chunk information in the VOX file
 * (not used anywhere outside of vox reader class)
 */
public class VoxChunk
{
    public int id;
    public int size;
    public int chunks;

    public VoxChunk(int id, int size, int chunks)
    {
        this.id = id;
        this.size = size;
        this.chunks = chunks;
    }

    @Override
    public String toString()
    {
        char[] chars = new char[] {(char) (this.id & 0xff), (char) ((this.id >> 8) & 0xff), (char) ((this.id >> 16) & 0xff), (char) ((this.id >> 24) & 0xff)};

        return new String(chars);
    }
}
