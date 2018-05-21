package mchorse.blockbuster.client.model.parsing;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.api.Model.Transform;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.ModelOBJRenderer;
import mchorse.blockbuster.client.model.parsing.obj.OBJMaterial;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser;
import mchorse.blockbuster.utils.TextureLocation;
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
    public File objModel;
    public File mtlFile;

    /**
     * Parse with default class 
     */
    public static void parse(String key, Model data)
    {
        parse(key, data, ModelCustom.class, null, null);
    }

    /**
     * Parse with default class 
     */
    public static void parse(String key, Model data, File objModel, File mtlFile)
    {
        parse(key, data, ModelCustom.class, objModel, mtlFile);
    }

    /**
     * Parse given input stream as JSON model, and then save this model in
     * the custom model repository
     */
    public static void parse(String key, Model data, Class<? extends ModelCustom> clazz, File objModel, File mtlFile)
    {
        try
        {
            ModelCustom model = new ModelParser(key, objModel, mtlFile).parseModel(data, clazz);
            ModelCustom.MODELS.put(key, model);
        }
        catch (Exception e)
        {
            System.out.println("Model for key '" + key + "' couldn't converted to ModelCustom!");
            e.printStackTrace();
        }
    }

    public ModelParser(String key, File objModel, File mtlFile)
    {
        this.key = key;
        this.objModel = objModel;
        this.mtlFile = mtlFile;
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

        Model.Pose standing = data.poses.get("standing");

        /* OBJ model support */
        Map<String, OBJParser.MeshObject> meshes = new HashMap<String, OBJParser.MeshObject>();

        if (this.objModel != null && this.objModel.isFile() && data.providesObj)
        {
            try
            {
                OBJParser parser = new OBJParser(this.objModel, data.providesMtl ? this.mtlFile : null);

                parser.read();
                meshes = parser.compile();

                /* Create a texture location for materials */
                for (OBJMaterial material : parser.materials.values())
                {
                    if (material.useTexture)
                    {
                        material.texture = new TextureLocation("blockbuster.actors", this.key + "/skins/" + material.name + "/default.png");

                        /* Create folder for every material */
                        new File(objModel.getParentFile(), "skins/" + material.name + "/").mkdirs();
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("An error occured OBJ model loading!");
                e.printStackTrace();
            }
        }

        /* First, iterate to create every limb */
        for (Map.Entry<String, Model.Limb> entry : data.limbs.entrySet())
        {
            Model.Limb limb = entry.getValue();
            Model.Transform transform = standing.limbs.get(entry.getKey());

            ModelCustomRenderer renderer = this.createRenderer(model, meshes, data, limb, transform);

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

        model.limbs = limbs.values().toArray(new ModelCustomRenderer[limbs.size()]);
        model.renderable = renderable.toArray(new ModelCustomRenderer[renderable.size()]);
    }

    /**
     * Create limb renderer for the model
     */
    protected ModelCustomRenderer createRenderer(ModelBase model, Map<String, OBJParser.MeshObject> meshes, Model data, Limb limb, Transform transform)
    {
        ModelCustomRenderer renderer;

        float w = limb.size[0];
        float h = limb.size[1];
        float d = limb.size[2];

        float ax = 1 - limb.anchor[0];
        float ay = limb.anchor[1];
        float az = limb.anchor[2];

        if (!meshes.isEmpty() && meshes.containsKey(limb.name))
        {
            renderer = new ModelOBJRenderer(model, limb, transform, meshes.get(limb.name));
        }
        else
        {
            renderer = new ModelCustomRenderer(model, limb, transform);
            renderer.mirror = limb.mirror;
            renderer.addBox(-ax * w, -ay * h, -az * d, (int) w, (int) h, (int) d);
        }

        renderer.applyTransform(transform);

        return renderer;
    }
}