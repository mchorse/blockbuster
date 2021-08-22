package mchorse.blockbuster.api.formats.vox;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.Mesh;
import mchorse.blockbuster.api.formats.vox.data.Vox;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.ModelVoxRenderer;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

public class MeshesVOX implements IMeshes
{
    public Mesh mesh;
    public VoxDocument document;
    public Vox vox;
    public Matrix3f rotation;

    public MeshesVOX(VoxDocument document, VoxDocument.LimbNode node)
    {
        this.document = document;
        this.vox = node.chunk;
        this.rotation = node.rotation;
    }

    @Override
    public ModelCustomRenderer createRenderer(Model data, ModelCustom model, ModelLimb limb, ModelTransform transform)
    {
        if (this.mesh == null)
        {
            this.mesh = new VoxBuilder(this.rotation).build(this.vox);
        }

        return new ModelVoxRenderer(model, limb, transform, this);
    }

    @Override
    public Vector3f getMin()
    {
        Vector3f min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

        for (int x = 0; x < this.vox.x; x++)
        {
            for (int y = 0; y < this.vox.y; y++)
            {
                for (int z = 0; z < this.vox.z; z++)
                {
                    if (this.vox.has(x, y, z))
                    {
                        min.x = Math.min(x, min.x);
                        min.y = Math.min(y, min.y);
                        min.z = Math.min(z, min.z);
                    }
                }
            }
        }

        return min;
    }

    @Override
    public Vector3f getMax()
    {
        Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        for (int x = 0; x < this.vox.x; x++)
        {
            for (int y = 0; y < this.vox.y; y++)
            {
                for (int z = 0; z < this.vox.z; z++)
                {
                    if (this.vox.has(x, y, z))
                    {
                        max.x = Math.max(x, max.x);
                        max.y = Math.max(y, max.y);
                        max.z = Math.max(z, max.z);
                    }
                }
            }
        }

        return max;
    }
}