package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.blockbuster.api.ModelTransform;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import net.minecraft.client.Minecraft;

public class GuiPoseTransformations extends GuiTransformations
{
    public ModelTransform trans;
    public ModelTransform oldTransform;

    public GuiPoseTransformations(Minecraft mc)
    {
        super(mc);
    }

    public void set(ModelTransform trans)
    {
        this.set(trans, null);
    }

    public void set(ModelTransform trans, ModelTransform oldTransform)
    {
        this.trans = trans;
        this.oldTransform = oldTransform;

        if (trans != null)
        {
            this.fillT(trans.translate[0], trans.translate[1], trans.translate[2]);
            this.fillS(trans.scale[0], trans.scale[1], trans.scale[2]);
            this.fillR(trans.rotate[0], trans.rotate[1], trans.rotate[2]);
        }
    }

    @Override
    public void setT(double x, double y, double z)
    {
        this.trans.translate[0] = (float) x;
        this.trans.translate[1] = (float) y;
        this.trans.translate[2] = (float) z;
    }

    @Override
    public void setS(double x, double y, double z)
    {
        this.trans.scale[0] = (float) x;
        this.trans.scale[1] = (float) y;
        this.trans.scale[2] = (float) z;
    }

    @Override
    public void setR(double x, double y, double z)
    {
        this.trans.rotate[0] = (float) x;
        this.trans.rotate[1] = (float) y;
        this.trans.rotate[2] = (float) z;
    }

    @Override
    protected void reset()
    {
        if (this.oldTransform == null)
        {
            super.reset();
        }
        else
        {
            this.fillSetT(this.oldTransform.translate[0], this.oldTransform.translate[1], this.oldTransform.translate[2]);
            this.fillSetS(this.oldTransform.scale[0], this.oldTransform.scale[1], this.oldTransform.scale[2]);
            this.fillSetR(this.oldTransform.rotate[0], this.oldTransform.rotate[1], this.oldTransform.rotate[2]);
        }
    }
}