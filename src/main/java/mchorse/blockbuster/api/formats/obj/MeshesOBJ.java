package mchorse.blockbuster.api.formats.obj;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.ModelOBJRenderer;
import net.minecraft.client.model.ModelBase;

import java.util.ArrayList;
import java.util.List;

public class MeshesOBJ implements IMeshes
{
    public List<MeshOBJ> meshes = new ArrayList<MeshOBJ>();

    public ModelCustomRenderer createRenderer(Model data, ModelBase model, ModelLimb limb, ModelTransform transform)
    {
        if (!data.providesObj)
        {
            return null;
        }

        return new ModelOBJRenderer(model, limb, transform, this);
    }
}
