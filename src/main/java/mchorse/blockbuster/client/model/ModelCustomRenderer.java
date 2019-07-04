package mchorse.blockbuster.client.model;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom model renderer class
 *
 * This class extended only for purpose of storing more
 */
@SideOnly(Side.CLIENT)
public class ModelCustomRenderer extends ModelRenderer
{
    private static float lastBrightnessX;
    private static float lastBrightnessY;

    public ModelLimb limb;
    public ModelTransform trasnform;
    public ModelCustomRenderer parent;

    public float scaleX = 1;
    public float scaleY = 1;
    public float scaleZ = 1;

    /* Compied code from the ModelRenderer */
    protected boolean compiled;
    protected int displayList = -1;

    public ModelCustomRenderer(ModelBase model, int texOffX, int texOffY)
    {
        super(model, texOffX, texOffY);
    }

    /**
     * Initiate with limb and transform instances
     */
    public ModelCustomRenderer(ModelBase model, ModelLimb limb, ModelTransform transform)
    {
        this(model, limb.texture[0], limb.texture[1]);

        this.limb = limb;
        this.trasnform = transform;
    }

    /**
     * Apply transformations on this model renderer
     */
    public void applyTransform(ModelTransform transform)
    {
        this.trasnform = transform;

        float x = transform.translate[0];
        float y = transform.translate[1];
        float z = transform.translate[2];

        this.rotationPointX = x;
        this.rotationPointY = this.limb.parent.isEmpty() ? (-y + 24) : -y;
        this.rotationPointZ = -z;

        this.rotateAngleX = transform.rotate[0] * (float) Math.PI / 180;
        this.rotateAngleY = -transform.rotate[1] * (float) Math.PI / 180;
        this.rotateAngleZ = -transform.rotate[2] * (float) Math.PI / 180;

        this.scaleX = transform.scale[0];
        this.scaleY = transform.scale[1];
        this.scaleZ = transform.scale[2];
    }

    @Override
    public void addChild(ModelRenderer renderer)
    {
        if (renderer instanceof ModelCustomRenderer)
        {
            ((ModelCustomRenderer) renderer).parent = this;
        }

        super.addChild(renderer);
    }

    /**
     * Setup state for current limb 
     */
    protected void setup()
    {
        GlStateManager.color(this.limb.color[0], this.limb.color[1], this.limb.color[2], this.limb.opacity);

        if (this.limb.opacity != 1.0F)
        {
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        else
        {
            GlStateManager.disableNormalize();
            GlStateManager.disableBlend();
        }

        if (!this.limb.lighting)
        {
            lastBrightnessX = OpenGlHelper.lastBrightnessX;
            lastBrightnessY = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }

        if (!this.limb.shading)
        {
            RenderHelper.disableStandardItemLighting();
        }

        if (this.limb.smooth)
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }
    }

    /**
     * Roll back the state to the way it was 
     */
    protected void disable()
    {
        if (!this.limb.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }

        if (!this.limb.shading)
        {
            GlStateManager.enableLighting();
            GlStateManager.enableLight(0);
            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial();
        }

        if (this.limb.smooth)
        {
            GL11.glShadeModel(GL11.GL_FLAT);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(float scale)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(scale);
                }

                GlStateManager.pushMatrix();
                GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
                {
                    if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F)
                    {
                        GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                        this.renderRenderer();

                        if (this.childModels != null)
                        {
                            for (int k = 0; k < this.childModels.size(); ++k)
                            {
                                this.childModels.get(k).render(scale);
                            }
                        }
                    }
                    else
                    {
                        GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                        GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                        this.renderRenderer();

                        if (this.childModels != null)
                        {
                            for (int j = 0; j < this.childModels.size(); ++j)
                            {
                                this.childModels.get(j).render(scale);
                            }
                        }

                        GlStateManager.translate(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);
                    }
                }
                else
                {
                    GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                    if (this.rotateAngleZ != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                    this.renderRenderer();

                    if (this.childModels != null)
                    {
                        for (int i = 0; i < this.childModels.size(); ++i)
                        {
                            this.childModels.get(i).render(scale);
                        }
                    }
                }

                GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderWithRotation(float scale)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(scale);
                }

                GlStateManager.pushMatrix();
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                if (this.rotateAngleY != 0.0F)
                {
                    GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F)
                {
                    GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }

                if (this.rotateAngleZ != 0.0F)
                {
                    GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                this.renderRenderer();
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Allows the changing of Angles after a box has been rendered
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void postRender(float scale)
    {
        if (this.parent != null)
        {
            this.parent.postRender(scale);
        }

        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(scale);
                }

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
                {
                    if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F)
                    {
                        GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                    }
                }
                else
                {
                    GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                    if (this.rotateAngleZ != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                    }
                }

                GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }
        }
    }

    /**
     * Compiles a GL display list for this model
     */
    protected void compileDisplayList(float scale)
    {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(this.displayList, 4864);
        BufferBuilder vertexbuffer = Tessellator.getInstance().getBuffer();

        for (int i = 0; i < this.cubeList.size(); ++i)
        {
            this.cubeList.get(i).render(vertexbuffer, scale);
        }

        GlStateManager.glEndList();
        this.compiled = true;
    }

    protected void renderRenderer()
    {
        if (this.limb.opacity <= 0)
        {
            return;
        }

        this.setup();
        this.renderDisplayList();
        this.disable();
    }

    /**
     * Render display list 
     */
    protected void renderDisplayList()
    {
        if (this.limb.is3D)
        {
            ModelExtrudedLayer.render3DLayer(this, RenderCustomModel.lastTexture);
        }
        else
        {
            GL11.glCallList(this.displayList);
        }
    }

    /**
     * DELET DIS 
     */
    public void delete()
    {
        if (this.displayList != -1)
        {
            GL11.glDeleteLists(this.displayList, 1);
        }
    }
}