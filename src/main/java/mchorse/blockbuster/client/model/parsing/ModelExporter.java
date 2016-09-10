package mchorse.blockbuster.client.model.parsing;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
     * Export renderer from given entity and its render
     */
    public String export(String name)
    {
        Model data = new Model();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
     * Save given limbs
     */
    private void savePose(String string, Model data, Map<String, ModelRenderer> limbs)
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

        data.poses.put(string, pose);
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

        for (ModelRenderer renderer : this.getModelRenderers(model))
        {
            Model.Limb limb = new Model.Limb();

            System.out.println("limb_" + i);

            limb.mirror = renderer.mirror;
            limb.size = this.getModelSize(renderer);
            limb.texture = this.getModelOffset(renderer);
            limb.anchor = this.getAnchor(renderer, limb.size);

            data.limbs.put("limb_" + i, limb);
            limbs.put("limb_" + i, renderer);

            i++;
        }

        return limbs;
    }

    /**
     * Compute model size based on the box in the model renderer
     */
    private int[] getModelSize(ModelRenderer renderer)
    {
        if (renderer.cubeList.size() == 0) return new int[] {0, 0, 0};

        ModelBox box = renderer.cubeList.get(0);

        int w = (int) (box.posX2 - box.posX1);
        int h = (int) (box.posY2 - box.posY1);
        int d = (int) (box.posZ2 - box.posZ1);

        return new int[] {w, h, d};
    }

    /**
     * Get texture offset of the model based on its box
     */
    private int[] getModelOffset(ModelRenderer renderer)
    {
        int[] zero = new int[] {0, 0};

        if (renderer.cubeList.size() == 0)
        {
            return zero;
        }

        /* Find the freaking TextureQuad that stores texture data */
        ModelBox box = renderer.cubeList.get(0);
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
    private float[] getAnchor(ModelRenderer renderer, int[] size)
    {
        if (renderer.cubeList.size() == 0) return new float[] {0.5F, 0.5F, 0.5F};

        ModelBox box = renderer.cubeList.get(0);

        float w = -box.posX1 / size[0];
        float h = -box.posY1 / size[1];
        float d = -box.posZ1 / size[2];

        System.out.println("anchor: " + w + ", " + h + ", " + d);
        System.out.println("size: " + size[0] + ", " + size[1] + ", " + size[2]);

        return new float[] {w, h, d};
    }

    /**
     * Get all model renderers that given model has
     */
    private List<ModelRenderer> getModelRenderers(ModelBase model)
    {
        List<ModelRenderer> renderers = new ArrayList<ModelRenderer>();

        for (Field fieldRenderer : model.getClass().getFields())
        {
            ModelRenderer renderer;

            if (!fieldRenderer.getType().isAssignableFrom(ModelRenderer.class)) continue;
            if (!fieldRenderer.isAccessible()) fieldRenderer.setAccessible(true);

            try
            {
                renderer = (ModelRenderer) fieldRenderer.get(model);
            }
            catch (Exception e)
            {
                e.printStackTrace();

                continue;
            }

            if (renderer != null && renderers.indexOf(renderer) == -1)
            {
                renderers.add(renderer);
            }
        }

        return renderers;
    }

    /**
     * Get first field that
     */
    private Field getFieldByType(Class type, Class subject)
    {
        for (Field field : subject.getDeclaredFields())
        {
            if (field.getType().equals(type)) return field;
        }

        return null;
    }
}
