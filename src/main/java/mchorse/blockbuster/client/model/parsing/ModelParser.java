package mchorse.blockbuster.client.model.parsing;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.api.ModelLimb.Holding;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.metamorph.Metamorph;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Model parser
 *
 * This class is responsible for converting models into in-game renderable
 * models (ModelCustom)
 */
@SideOnly(Side.CLIENT)
public class ModelParser
{
    public String key;
    public Map<String, IMeshes> meshes;

    /**
     * Parse with default class 
     */
    public static ModelCustom parse(String key, Model data)
    {
        return parse(key, data, ModelCustom.class, null);
    }

    /**
     * Parse with default class 
     */
    public static ModelCustom parse(String key, Model data, Map<String, IMeshes> meshes)
    {
        return parse(key, data, ModelCustom.class, meshes);
    }

    /**
     * Parse given input stream as JSON model, and then save this model in
     * the custom model repository
     */
    public static ModelCustom parse(String key, Model data, Class<? extends ModelCustom> clazz, Map<String, IMeshes> meshes)
    {
        try
        {
            return new ModelParser(key, meshes).parseModel(data, clazz);
        }
        catch (Exception e)
        {
            System.out.println("Model for key '" + key + "' couldn't converted to ModelCustom!");
            e.printStackTrace();
        }

        return null;
    }

    public ModelParser(String key, Map<String, IMeshes> meshes)
    {
        if (meshes == null)
        {
            meshes = new HashMap<String, IMeshes>();
        }

        this.key = key;
        this.meshes = meshes;
    }

    /**
     * Parse and build model out of given JSON string. Throws exception in case
     * if parsed model doesn't have at least one required pose.
     */
    public ModelCustom parseModel(Model data, Class<? extends ModelCustom> clazz) throws Exception
    {
        ModelCustom model = clazz.getConstructor(Model.class).newInstance(data);
        this.generateLimbs(data, model);

        if (model instanceof IModelCustom)
        {
            ((IModelCustom) model).onGenerated();
        }

        return model;
    }

    /**
     * Generate limbs for a custom model renderer based on a passed model data
     * which was parsed from JSON.
     */
    protected void generateLimbs(Model data, ModelCustom model)
    {
        /* Define lists for different purposes */
        Map<String, ModelCustomRenderer> limbs = new HashMap<String, ModelCustomRenderer>();
        List<ModelRenderer> renderable = new ArrayList<ModelRenderer>();

        List<ModelRenderer> left = new ArrayList<ModelRenderer>();
        List<ModelRenderer> right = new ArrayList<ModelRenderer>();
        List<ModelRenderer> armor = new ArrayList<ModelRenderer>();

        ModelPose standing = data.poses.get("standing");

        /* First, iterate to create every limb */
        for (Map.Entry<String, ModelLimb> entry : data.limbs.entrySet())
        {
            ModelLimb limb = entry.getValue();
            ModelTransform transform = standing.limbs.get(entry.getKey());

            ModelCustomRenderer renderer = this.createRenderer(model, data, limb, transform);

            if (limb.holding == Holding.LEFT) left.add(renderer);
            if (limb.holding == Holding.RIGHT) right.add(renderer);
            if (limb.slot != ArmorSlot.NONE) armor.add(renderer);

            limbs.put(entry.getKey(), renderer);
        }

        /* Then, iterate to attach child to their parents */
        for (Map.Entry<String, ModelCustomRenderer> entry : limbs.entrySet())
        {
            ModelLimb limb = data.limbs.get(entry.getKey());

            if (!limb.parent.isEmpty())
            {
                limbs.get(limb.parent).addChild(entry.getValue());
            }
            else
            {
                renderable.add(entry.getValue());
            }

            /* Inject ModelCustomRenderers into the model's fields */
            if (model instanceof IModelCustom)
            {
                try
                {
                    Field field = model.getClass().getField(entry.getKey());

                    if (field != null)
                    {
                        field.set(model, entry.getValue());
                    }
                }
                catch (Exception e)
                {
                    Metamorph.log("Field '" + entry.getKey() + "' was not found or is not accessible for " + model.getClass().getSimpleName());
                }
            }
        }

        /* Assign values */
        model.left = left.toArray(new ModelCustomRenderer[left.size()]);
        model.right = right.toArray(new ModelCustomRenderer[right.size()]);
        model.armor = armor.toArray(new ModelCustomRenderer[armor.size()]);

        model.limbs = limbs.values().toArray(new ModelCustomRenderer[limbs.size()]);
        model.renderable = renderable.toArray(new ModelCustomRenderer[renderable.size()]);
    }

    /**
     * Create limb renderer for the model
     */
    protected ModelCustomRenderer createRenderer(ModelBase model, Model data, ModelLimb limb, ModelTransform transform)
    {
        ModelCustomRenderer renderer = null;

        float w = limb.size[0];
        float h = limb.size[1];
        float d = limb.size[2];

        float ax = 1 - limb.anchor[0];
        float ay = limb.anchor[1];
        float az = limb.anchor[2];

        IMeshes meshes = this.meshes.get(limb.name);

        if (meshes != null)
        {
            renderer = meshes.createRenderer(data, model, limb, transform);
        }

        if (renderer == null)
        {
            renderer = new ModelCustomRenderer(model, limb, transform);
            renderer.mirror = limb.mirror;
            renderer.addBox(-ax * w, -ay * h, -az * d, (int) w, (int) h, (int) d, limb.sizeOffset);
        }

        renderer.applyTransform(transform);

        return renderer;
    }
}