package mchorse.blockbuster.api.formats;

/**
 * Holds the mesh data
 */
public class Mesh
{
    public float[] posData;
    public float[] texData;
    public float[] normData;
    public int triangles;

    public Mesh(int triangles)
    {
        this(new float[triangles * 9], new float[triangles * 6], new float[triangles * 9]);
    }

    public Mesh(float[] posData, float[] texData, float[] normData)
    {
        this.posData = posData;
        this.texData = texData;
        this.normData = normData;

        this.triangles = posData.length / 3;
    }
}
