package mchorse.blockbuster.api.formats;

/**
 * Holds the mesh data
 */
public class Mesh
{
    public float[] posData;
    public float[] texData;
    public float[] normData;

    public Mesh(int faces)
    {
        this(new float[faces * 9], new float[faces * 6], new float[faces * 9]);
    }

    public Mesh(float[] posData, float[] texData, float[] normData)
    {
        this.posData = posData;
        this.texData = texData;
        this.normData = normData;
    }
}
