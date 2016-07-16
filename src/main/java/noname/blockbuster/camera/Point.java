package noname.blockbuster.camera;

import com.google.common.base.Objects;

public class Point
{
    public float x;
    public float y;
    public float z;

    public Point(float x, float y, float z)
    {
        this.set(x, y, z);
    }

    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y).add("z", this.z).toString();
    }
}
