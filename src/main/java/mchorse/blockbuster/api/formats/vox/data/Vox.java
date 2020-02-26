package mchorse.blockbuster.api.formats.vox.data;

public class Vox
{
    public int x;
    public int y;
    public int z;

    public int[] voxels;

    public int toIndex(int x, int y, int z)
    {
        return x + y * this.x + z * this.x * this.y;
    }

    public boolean has(int x, int y, int z)
    {
        return x >= 0 && y >= 0 && z >= 0 && x < this.x && y < this.y && z < this.z && this.voxels[this.toIndex(x, y, z)] != 0;
    }
}