package mchorse.blockbuster.client.model.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.model.ModelRenderer;

/**
 * Model parser
 *
 * This class is responsible for converting models into in-game renderable
 * models (ModelCustom)
 */
@SideOnly(Side.CLIENT)
public class ModelParser
{
    /**
     * Parse given input stream as JSON model, and then save this model in
     * the custom model repository
     */
    public static void parse(String key, Model data)
    {
        try
        {
            ModelCustom model = new ModelParser().parseModel(data);
            ModelCustom.MODELS.put(key, model);
        }
        catch (Exception e)
        {
            System.out.println("Model for key '" + key + "' couldn't converted to ModelCustom!");
            e.printStackTrace();
        }
    }

    /**
     * Parse and build model out of given JSON string. Throws exception in case
     * if parsed model doesn't have at least one required pose.
     */
    public ModelCustom parseModel(Model data) throws Exception
    {
        ModelCustom model = new ModelCustom(data);
        this.generateLimbs(data, model);

        return model;
    }

    /**
     * Generate limbs for a custom model renderer based on a passed model data
     * which was parsed from JSON.
     */
    private void generateLimbs(Model data, ModelCustom model)
    {
        /* Define lists for different purposes */
        Map<String, ModelCustomRenderer> limbs = new HashMap<String, ModelCustomRenderer>();
        List<ModelRenderer> renderable = new ArrayList<ModelRenderer>();

        List<ModelRenderer> left = new ArrayList<ModelRenderer>();
        List<ModelRenderer> right = new ArrayList<ModelRenderer>();

        Model.Pose standing = data.poses.get("standing");

        /* First, iterate to create every limb */
        for (Map.Entry<String, Model.Limb> entry : data.limbs.entrySet())
        {
            Model.Limb limb = entry.getValue();
            Model.Transform transform = standing.limbs.get(entry.getKey());
            ModelCustomRenderer renderer = new ModelCustomRenderer(model, limb, transform);

            float w = limb.size[0];
            float h = limb.size[1];
            float d = limb.size[2];

            float ax = 1 - limb.anchor[0];
            float ay = limb.anchor[1];
            float az = limb.anchor[2];

            renderer.mirror = limb.mirror;
            renderer.addBox(-ax * w, -ay * h, -az * d, (int) w, (int) h, (int) d);
            renderer.applyTransform(transform);

            if (limb.holding.equals("left")) left.add(renderer);
            if (limb.holding.equals("right")) right.add(renderer);

            limbs.put(entry.getKey(), renderer);
        }

        /* Then, iterate to attach child to their parents */
        for (Map.Entry<String, ModelCustomRenderer> entry : limbs.entrySet())
        {
            Model.Limb limb = data.limbs.get(entry.getKey());

            if (!limb.parent.isEmpty())
            {
                limbs.get(limb.parent).addChild(entry.getValue());
            }
            else
            {
                renderable.add(entry.getValue());
            }
        }

        /* Assign values */
        model.left = left.toArray(new ModelCustomRenderer[left.size()]);
        model.right = right.toArray(new ModelCustomRenderer[right.size()]);

        model.limbs = limbs.values().toArray(new ModelCustomRenderer[limbs.size()]);
        model.renderable = renderable.toArray(new ModelCustomRenderer[renderable.size()]);
    }
}