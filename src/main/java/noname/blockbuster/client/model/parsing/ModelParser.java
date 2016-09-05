package noname.blockbuster.client.model.parsing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.model.ModelCustom;
import noname.blockbuster.client.model.ModelCustomRenderer;

/**
 * Model parser
 *
 * This class is responsible for parsing JSON models and
 */
@SideOnly(Side.CLIENT)
public class ModelParser
{
    /**
     * Parse the model
     */
    public static void parse(String key, InputStream stream)
    {
        ModelParser parser = new ModelParser();

        Scanner scanner = new Scanner(stream, "UTF-8");

        ModelCustom test = parser.parseModel(scanner.useDelimiter("\\A").next());
        ModelCustom.MODELS.put(key, test);

        scanner.close();
    }

    /**
     * Parse and build model out of given JSON string
     */
    public ModelCustom parseModel(String json)
    {
        Gson gson = new GsonBuilder().create();

        Model data = gson.fromJson(json, Model.class);
        ModelCustom model = new ModelCustom(data.texture[0], data.texture[1]);

        data.fillInMissing();
        this.generateLimbs(data, model);

        return model;
    }

    /**
     * Generate limbs for a custom model renderer based on a passed model data
     * which was parsed from JSON.
     */
    private void generateLimbs(Model data, ModelCustom model)
    {
        Map<String, ModelCustomRenderer> limbs = new HashMap<String, ModelCustomRenderer>();
        Model.Pose standing = data.poses.get("standing");

        /* First, iterate to create every limb */
        for (Map.Entry<String, Model.Limb> entry : data.limbs.entrySet())
        {
            Model.Limb limb = entry.getValue();
            Model.Transform transform = standing.limbs.get(entry.getKey());

            ModelCustomRenderer renderer = new ModelCustomRenderer(model, limb, transform);

            float x = transform.translate[0];
            float y = transform.translate[1];
            float z = transform.translate[2];

            float w = limb.size[0];
            float h = limb.size[1];
            float d = limb.size[2];

            float ax = limb.anchor[0];
            float ay = limb.anchor[1];
            float az = limb.anchor[2];

            renderer.mirror = limb.mirror;
            renderer.addBox(-ax * w, -ay * h, -az * d, (int) w, (int) h, (int) d);

            renderer.offsetX = x / 16;
            renderer.offsetY = limb.parent.isEmpty() ? (-y + 24) / 16 : -y / 16;
            renderer.offsetZ = -z / 16;

            renderer.rotateAngleX = transform.rotate[0] * (float) Math.PI / 180;
            renderer.rotateAngleY = transform.rotate[1] * (float) Math.PI / 180;
            renderer.rotateAngleZ = transform.rotate[2] * (float) Math.PI / 180;

            limbs.put(entry.getKey(), renderer);
        }

        List<ModelRenderer> renderable = new ArrayList<ModelRenderer>();

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

        model.limbs = limbs.values().toArray(new ModelCustomRenderer[limbs.size()]);
        model.renderable = renderable.toArray(new ModelCustomRenderer[renderable.size()]);
    }
}
