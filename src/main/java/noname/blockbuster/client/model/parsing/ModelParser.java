package noname.blockbuster.client.model.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.model.ModelCustom;

/**
 * Model parser
 *
 * This class is responsible for parsing JSON models and
 */
@SideOnly(Side.CLIENT)
public class ModelParser
{
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
        List<ModelRenderer> limbs = new ArrayList<ModelRenderer>();
        Model.Pose standing = data.poses.get("standing");

        for (Map.Entry<String, Model.Limb> set : data.limbs.entrySet())
        {
            Model.Limb limb = set.getValue();
            Model.Transform transform = standing.limbs.get(set.getKey());

            ModelRenderer renderer = new ModelRenderer(model, limb.texture[0], limb.texture[1]);

            float x = transform.translate[0];
            float y = transform.translate[1];
            float z = transform.translate[2];

            x -= limb.anchor[0] * limb.size[0];
            y += limb.anchor[1] * limb.size[1];
            z -= limb.anchor[2] * limb.size[2];

            renderer.addBox(x, -y + 24, z, limb.size[0], limb.size[1], limb.size[2]);
            renderer.mirror = limb.mirror;

            limbs.add(renderer);
        }

        model.limbs = limbs.toArray(new ModelRenderer[limbs.size()]);
    }
}
