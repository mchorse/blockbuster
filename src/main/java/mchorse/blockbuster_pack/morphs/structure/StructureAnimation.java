package mchorse.blockbuster_pack.morphs.structure;

import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.metamorph.api.morphs.utils.Animation;

public class StructureAnimation extends Animation
{
    public ModelTransform last = new ModelTransform();

    public void merge(StructureMorph last, StructureMorph next)
    {
        this.merge(next.animation);
        this.last.copy(last.pose);
    }

    public void apply(ModelTransform transform, float partialTicks)
    {
        float factor = this.getFactor(partialTicks);

        transform.interpolate(this.last, transform, factor, this.interp);
    }
}