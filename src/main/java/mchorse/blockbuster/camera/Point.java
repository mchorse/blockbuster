package mchorse.blockbuster.camera;

import com.google.common.base.Objects;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Point class
 *
 * This class represents a point in 3 dimensional space. This point class
 * used by {@link Position} class to represent coordinates for fixtures.
 */
public class Point
{
    public float x;
    public float y;
    public float z;

    public Point(float x, float y, float z)
    {
        this.set(x, y, z);
    }

    public Point(EntityPlayer player)
    {
        this.set(player);
    }

    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(EntityPlayer player)
    {
        this.set((float) player.posX, (float) player.posY, (float) player.posZ);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.x).addValue(this.y).addValue(this.z).toString();
    }
}
