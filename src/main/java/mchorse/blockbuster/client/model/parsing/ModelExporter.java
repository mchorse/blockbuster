package mchorse.blockbuster.client.model.parsing;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

/**
 * Model exporter
 *
 * Model exporter class is responsible for exporting Minecraft in-game models
 * based on the values of ModelBase ModelRenderer's ModelBoxes and given entity.
 */
public class ModelExporter
{
    private EntityLivingBase entity;
    private RenderLivingBase<EntityLivingBase> render;

    public ModelExporter(EntityLivingBase entity, RenderLivingBase<EntityLivingBase> render)
    {
        this.entity = entity;
        this.render = render;
    }

    /**
     * Get main model
     */
    private ModelBase getModel()
    {
        return this.render.getMainModel();
    }

    /**
     * Main method of this class.
     *
     * This method is responsible for exporting a JSON model based on the values
     * of given renderer's model and entity state.
     *
     * See private methods for more information about this export process.
     */
    public String export(String name)
    {
        Model data = new Model();
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();

        this.render.doRender(this.entity, 0, -420, 0, 0, 0);
        ModelBase model = this.getModel();

        this.setupProperties(data, model, name);
        Map<String, ModelRenderer> limbs = this.generateLimbs(data, model);

        /* Save standing, sleeping and flying poses */
        this.savePose("standing", data, limbs);
        this.savePose("sleeping", data, limbs);
        this.savePose("flying", data, limbs);

        /* Save sneaking pose */
        this.entity.setSneaking(true);
        this.savePose("sneaking", data, limbs);

        return gson.toJson(data);
    }

    /**
     * Save pose transformations for every limb
     */
    private void savePose(String poseName, Model data, Map<String, ModelRenderer> limbs)
    {
        Model.Pose pose = new Model.Pose();

        /* Set size */
        float width = this.entity.width;
        float height = this.entity.height;

        pose.size = new float[] {width, height, width};

        /* Allocate transforms */
        for (Map.Entry<String, ModelRenderer> entry : limbs.entrySet())
        {
            String key = entry.getKey();
            ModelRenderer renderer = entry.getValue();
            Model.Transform transform = new Model.Transform();

            float PI = (float) Math.PI;

            float rx = renderer.rotateAngleX * 180 / PI;
            float ry = renderer.rotateAngleY * 180 / PI;
            float rz = renderer.rotateAngleZ * 180 / PI;

            float x = renderer.rotationPointX;
            float y = renderer.rotationPointY;
            float z = renderer.rotationPointZ;

            transform.rotate = new float[] {rx, ry, rz};
            transform.translate = new float[] {x, -(y - 24), -z};

            pose.limbs.put(key, transform);
        }

        data.poses.put(poseName, pose);
    }

    /**
     * Setup main properties for given model
     */
    private void setupProperties(Model data, ModelBase model, String name)
    {
        data.name = name;
        data.texture = new int[] {model.textureWidth, model.textureHeight};
    }

    /**
     * Generate limbs from the given model
     */
    private Map<String, ModelRenderer> generateLimbs(Model data, ModelBase model)
    {
        Map<String, ModelRenderer> limbs = new HashMap<String, ModelRenderer>();
        int i = 0;

        int width = 0;
        int height = 0;

        for (ModelRenderer renderer : this.getModelRenderers(model))
        {
            int j = 0;

            for (ModelBox box : renderer.cubeList)
            {
                Model.Limb limb = new Model.Limb();

                if (j == 0)
                {
                    limb.mirror = renderer.mirror;
                }

                limb.size = this.getModelSize(box);
                limb.texture = this.getModelOffset(box, renderer);
                limb.anchor = this.getAnchor(box, limb.size);

                data.limbs.put("limb_" + i, limb);
                limbs.put("limb_" + i, renderer);

                j++;
                i++;
            }

            width = Math.max(width, (int) renderer.textureWidth);
            height = Math.max(width, (int) renderer.textureHeight);
        }

        /* Some bastard decided that it was a smart idea to define two variables
         * in Model's constructor and inject those values directly into the
         * ModelRenderer via setTextureSize() instead of setting model's
         * properties textureWidth and textureHeight. Therefore ModelBase
         * properties has misleading result.
         *
         * This is a workaround to inject the right texture size for exporting
         * JSON model. Basically, if the texture size from JSON model doesn't
         * correspond with values that has been set from ModelRenderers,
         * it uses the greater value that was got from ModelRenderers.
         *
         * Zero check for width and height is just in case.
         *
         * See ModelIronGolem for more information. I hope it wasn't jeb.
         */
        if (data.texture[0] != width || data.texture[1] != height && width != 0 && height != 0)
        {
            data.texture = new int[] {width, height};
        }

        return limbs;
    }

    /**
     * Compute model size based on the box in the model renderer
     */
    private int[] getModelSize(ModelBox box)
    {
        int w = (int) (box.posX2 - box.posX1);
        int h = (int) (box.posY2 - box.posY1);
        int d = (int) (box.posZ2 - box.posZ1);

        return new int[] {w, h, d};
    }

    /**
     * Get texture offset of the model based on its box
     */
    private int[] getModelOffset(ModelBox box, ModelRenderer renderer)
    {
        int[] zero = new int[] {0, 0};

        Field field = this.getFieldByType(TexturedQuad[].class, ModelBox.class);
        TexturedQuad[] quads;

        field.setAccessible(true);

        try
        {
            quads = (TexturedQuad[]) field.get(box);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return zero;
        }

        /* Getting the minimum */
        float minX = 1.0F;
        float minY = 1.0F;

        for (TexturedQuad quad : quads)
        {
            for (PositionTextureVertex vertex : quad.vertexPositions)
            {
                minX = Math.min(vertex.texturePositionX, minX);
                minY = Math.min(vertex.texturePositionY, minY);
            }
        }

        minX *= renderer.textureWidth;
        minY *= renderer.textureHeight;

        return new int[] {(int) minX, (int) minY};
    }

    /**
     * Compute anchor based on the renderer
     */
    private float[] getAnchor(ModelBox box, int[] size)
    {
        float w = -box.posX1 / size[0];
        float h = -box.posY1 / size[1];
        float d = -box.posZ1 / size[2];

        return new float[] {w, h, d};
    }

    /**
     * Get all model renderers that given model has (even if that is an array
     * of ModelRenderers)
     */
    private List<ModelRenderer> getModelRenderers(ModelBase model)
    {
        List<ModelRenderer> renderers = new ArrayList<ModelRenderer>();

        Class<?> rClass = ModelRenderer.class;
        Class<?> aRClass = ModelRenderer[].class;

        for (Field field : getInheritedFields(model.getClass()))
        {
            Class<?> type = field.getType();

            if (!type.isAssignableFrom(rClass) && !type.isAssignableFrom(aRClass)) continue;
            if (!field.isAccessible()) field.setAccessible(true);

            System.out.println(field.getName() + ": " + field.getType().getSimpleName());

            try
            {
                if (type.isAssignableFrom(rClass))
                {
                    ModelRenderer renderer = (ModelRenderer) field.get(model);

                    if (renderer != null && renderers.indexOf(renderer) == -1)
                    {
                        renderers.add(renderer);
                    }
                }
                else if (type.isAssignableFrom(aRClass))
                {
                    ModelRenderer[] moreRenderers = (ModelRenderer[]) field.get(model);

                    for (ModelRenderer renderer : moreRenderers)
                    {
                        if (renderer != null && renderers.indexOf(renderer) == -1)
                        {
                            renderers.add(renderer);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();

                continue;
            }
        }

        return renderers;
    }

    /**
     * Get first field that corresponds to given class type in given class
     * subject
     */
    private Field getFieldByType(Class<?> type, Class<?> subject)
    {
        for (Field field : subject.getDeclaredFields())
        {
            if (field.getType().equals(type)) return field;
        }

        return null;
    }

    /**
     * From StackOverflow
     */
    public static List<Field> getInheritedFields(Class<?> type)
    {
        List<Field> fields = new ArrayList<Field>();

        for (Class<?> c = type; c != null; c = c.getSuperclass())
        {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }

        return fields;
    }
}