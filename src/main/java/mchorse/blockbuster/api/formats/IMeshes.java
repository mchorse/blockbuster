package mchorse.blockbuster.api.formats;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;

import javax.vecmath.Vector3f;

public interface IMeshes
{
    public ModelCustomRenderer createRenderer(Model data, ModelCustom model, ModelLimb limb, ModelTransform transform);

    public Vector3f getMin();

    public Vector3f getMax();
}