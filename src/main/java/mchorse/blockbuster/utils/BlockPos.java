package mchorse.blockbuster.utils;

public class BlockPos
{
    public int x;
    public int y;
    public int z;

    public BlockPos(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }

    public BlockPos offset(int sideHit)
    {
        BlockPos pos = new BlockPos(this.x, this.y, this.z);

        if (sideHit == 0)
        {
            pos.y += 1;
        }
        else if (sideHit == 1)
        {
            pos.y -= 1;
        }
        else if (sideHit == 2)
        {
            pos.x += 1;
        }
        else if (sideHit == 3)
        {
            pos.x -= 1;
        }
        else if (sideHit == 4)
        {
            pos.z += 1;
        }
        else if (sideHit == 5)
        {
            pos.z -= 1;
        }

        return pos;
    }
}