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
}
