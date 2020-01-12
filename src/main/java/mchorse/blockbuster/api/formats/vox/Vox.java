package mchorse.blockbuster.api.formats.vox;

public class Vox
{
    public int[] voxels;

    /**
     * RGBA palette
     */
    public int[] palette;
    public int x;
    public int y;
    public int z;

    public int toIndex(int x, int y, int z)
    {
        return x + y * this.x + z * this.x * this.y;
    }

    public boolean has(int x, int y, int z)
    {
        return x >= 0 && y >= 0 && z >= 0 && x < this.x && y < this.y && z < this.z && this.voxels[this.toIndex(x, y, z)] != 0;
    }

    public static class VoxChunk
    {
        public int id;
        public int size;
        public int chunks;

        @Override
        public String toString()
        {
            char[] chars = new char[] {(char) (this.id & 0xff), (char) ((this.id >> 8) & 0xff), (char) ((this.id >> 16) & 0xff), (char) ((this.id >> 24) & 0xff)};

            return new String(chars);
        }
    }
}