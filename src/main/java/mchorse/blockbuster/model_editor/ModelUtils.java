package mchorse.blockbuster.model_editor;

import java.util.Map;

import mchorse.metamorph.api.models.Model;
import net.minecraft.util.ResourceLocation;

/**
 * Model utilities
 *
 * This code might be transferred to Metamorph, since this code is actually
 * supposed to be in {@link Model} class.
 */
public class ModelUtils
{
    /**
     * Clone a model
     */
    public static Model cloneModel(Model a)
    {
        Model b = new Model();

        b.texture = new int[] {a.texture[0], a.texture[1]};
        b.scale = new float[] {a.scale[0], a.scale[1], a.scale[2]};

        b.name = a.name;
        b.scheme = a.scheme;
        b.model = a.model;

        b.defaultTexture = a.defaultTexture == null ? null : new ResourceLocation(a.defaultTexture.toString());

        for (Map.Entry<String, Model.Limb> entry : a.limbs.entrySet())
        {
            b.limbs.put(entry.getKey(), cloneLimb(entry.getValue()));
        }

        for (Map.Entry<String, Model.Pose> entry : a.poses.entrySet())
        {
            b.poses.put(entry.getKey(), clonePose(entry.getValue()));
        }

        return a;
    }

    /**
     * Clone a model limb
     */
    public static Model.Limb cloneLimb(Model.Limb a)
    {
        Model.Limb b = new Model.Limb();

        b.anchor = new float[] {a.anchor[0], a.anchor[1], a.anchor[2]};
        b.size = new int[] {a.size[0], a.size[1], a.size[2]};
        b.texture = new int[] {a.texture[0], a.texture[1]};

        b.idle = a.idle;
        b.invert = a.invert;
        b.looking = a.looking;
        b.mirror = a.mirror;
        b.swinging = a.swinging;
        b.swiping = a.swiping;

        b.name = a.name;
        b.parent = a.parent;

        return b;
    }

    /**
     * Clone a model pose
     */
    public static Model.Pose clonePose(Model.Pose a)
    {
        Model.Pose b = new Model.Pose();

        b.size = new float[] {a.size[0], a.size[1], a.size[2]};

        for (Map.Entry<String, Model.Transform> entry : a.limbs.entrySet())
        {
            b.limbs.put(entry.getKey(), cloneTransform(entry.getValue()));
        }

        return b;
    }

    /**
     * Clone a model transform
     */
    public static Model.Transform cloneTransform(Model.Transform a)
    {
        Model.Transform b = new Model.Transform();

        b.translate = new float[] {a.translate[0], a.translate[1], a.translate[2]};
        b.rotate = new float[] {a.rotate[0], a.rotate[1], a.rotate[2]};
        b.scale = new float[] {a.scale[0], a.scale[1], a.scale[2]};

        return b;
    }
}