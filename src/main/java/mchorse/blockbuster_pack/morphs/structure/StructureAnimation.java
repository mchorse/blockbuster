package mchorse.blockbuster_pack.morphs.structure;

import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.metamorph.api.morphs.utils.Animation;

public class StructureAnimation extends Animation
{
    public ModelTransform last;
    public Float lastAnchorX;
    public Float lastAnchorY;
    public Float lastAnchorZ;

    @Override
    public void reset()
    {
        super.reset();

        this.last = null;
        this.lastAnchorX = null;
        this.lastAnchorY = null;
        this.lastAnchorZ = null;
    }

    public void merge(StructureMorph last, StructureMorph next)
    {
        this.merge(next.animation);
        this.last = new ModelTransform();
        this.last.copy(last.pose);
        this.lastAnchorX = last.anchorX;
        this.lastAnchorY = last.anchorY;
        this.lastAnchorZ = last.anchorZ;
    }

    public void apply(ModelTransform transform, float partialTicks)
    {
        if (this.last != null)
        {
            float factor = this.getFactor(partialTicks);

            transform.interpolate(this.last, transform, factor, this.interp);
        }
    }
}