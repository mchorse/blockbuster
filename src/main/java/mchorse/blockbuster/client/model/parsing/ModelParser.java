package mchorse.blockbuster.client.model.parsing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import mchorse.blockbuster.actor.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Model parser
 *
 * This class is responsible for parsing JSON models
 */
@SideOnly(Side.CLIENT)
public class ModelParser
{
    /**
     * Parse given input stream as JSON model, and then save this model in
     * the custom model repository
     */
    public static void parse(String key, InputStream stream)
    {
        try
        {
            ModelParser parser = new ModelParser();

            Scanner scanner = new Scanner(stream, "UTF-8");

            ModelCustom model = parser.parseModel(scanner.useDelimiter("\\A").next());
            ModelCustom.MODELS.put(key, model);

            scanner.close();
        }
        catch (Exception e)
        {
            System.out.println("JSON model for key '" + key + "' couldn't be loaded!");
            e.printStackTrace();
        }
    }

    public static void parse(String key, Model data)
    {
        try
        {
            ModelParser parser = new ModelParser();

            ModelCustom model = parser.parseModel(data);
            ModelCustom.MODELS.put(key, model);
        }
        catch (Exception e)
        {
            System.out.println("JSON model for key '" + key + "' couldn't be loaded!");
            e.printStackTrace();
        }
    }

    /**
     * Parse and build model out of given JSON string. Throws exception in case
     * if parsed model doesn't have at least one required pose.
     */
    public ModelCustom parseModel(String json) throws Exception
    {
        return this.parseModel(Model.parse(json));
    }

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
            renderer.addBox(-ax * w, -ay * h, -az * d, (int) w, (int) h, (int) d, (transform.scale[0] * w - w) / 2);
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