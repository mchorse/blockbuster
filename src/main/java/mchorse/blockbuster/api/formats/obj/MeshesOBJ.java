package mchorse.blockbuster.api.formats.obj;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.ModelOBJRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeshesOBJ implements IMeshes
{
    public List<MeshOBJ> meshes = new ArrayList<MeshOBJ>();
    public Map<String, List<MeshOBJ>> shapes;

    public ModelCustomRenderer createRenderer(Model data, ModelCustom model, ModelLimb limb, ModelTransform transform)
    {
        if (!data.providesObj)
        {
            return null;
        }

        return new ModelOBJRenderer(model, limb, transform, this);
    }

    public void mergeShape(String name, MeshesOBJ shape)
    {
        this.shapes = this.shapes == null ? new HashMap<String, List<MeshOBJ>>() : this.shapes;
        this.shapes.put(name, shape.meshes);
    }

    @Override
    public javax.vecmath.Vector3f getMin()
    {
        javax.vecmath.Vector3f min = new javax.vecmath.Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

        for (MeshOBJ obj : this.meshes)
        {
            for (int i = 0, c = obj.posData.length / 3; i < c; i++)
            {
                min.x = Math.min(obj.posData[i * 3], min.x);
                min.y = Math.min(obj.posData[i * 3 + 1], min.y);
                min.z = Math.min(obj.posData[i * 3 + 2], min.z);
            }
        }

        return min;
    }

    @Override
    public javax.vecmath.Vector3f getMax()
    {
        javax.vecmath.Vector3f max = new javax.vecmath.Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        for (MeshOBJ obj : this.meshes)
        {
            for (int i = 0, c = obj.posData.length / 3; i < c; i++)
            {
                max.x = Math.max(obj.posData[i * 3], max.x);
                max.y = Math.max(obj.posData[i * 3 + 1], max.y);
                max.z = Math.max(obj.posData[i * 3 + 2], max.z);
            }
        }

        return max;
    }
}
