package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.formats.obj.ShapeKey;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiAnchorModal;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.client.render.layer.LayerHeldItem;
import mchorse.mclib.client.Draw;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.Interpolations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Model renderer which renders Blockbuster models 
 */
public class GuiBBModelRenderer extends GuiModelRenderer
{    
    public boolean swinging;
    private float swing;
    private float swingAmount;
    private int swipe;

    public boolean items;
    public boolean aabb;
    public boolean origin;
    public boolean looking = true;

    public Map<String, ResourceLocation> materials;
    public ResourceLocation texture;
    public ModelCustom model;
    public ModelLimb limb;
    public GuiAnchorModal anchorPreview;

    private ModelPose pose;
    private List<ShapeKey> shapes;

    public static void renderItems(EntityLivingBase entity, ModelCustom model)
    {
        ItemStack main = entity.getHeldItemMainhand();
        ItemStack offhand = entity.getHeldItemOffhand();

        if (!offhand.isEmpty() || !main.isEmpty())
        {
            GlStateManager.pushMatrix();
            LayerHeldItem.renderHeldItem(entity, offhand, model, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            LayerHeldItem.renderHeldItem(entity, main, model, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            GlStateManager.popMatrix();
        }
    }

    public GuiBBModelRenderer(Minecraft mc)
    {
        super(mc);
    }

    public void swipe()
    {
        this.swipe = 6;
    }

    public void toggleItems()
    {
        this.items = !this.items;
        ((DummyEntity) this.entity).toggleItems(this.items);
    }

    /**
     * Update logic
     */
    @Override
    protected void update()
    {
        super.update();

        if (this.swipe > -1)
        {
            this.swipe--;
        }

        if (this.swinging)
        {
            this.swing += 0.75F;
            this.swingAmount = 1.0F;
        }
        else
        {
            this.swing = 0.0F;
            this.swingAmount = 0.0F;
        }
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        boolean result = super.mouseClicked(context);

        if (this.dragging && GuiScreen.isCtrlKeyDown())
        {
            this.tryPicking = true;
            this.dragging = false;
        }

        return result;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);
        this.tryPicking = false;
    }

    protected float getScale()
    {
        return 1;
    }

    @Override
    protected void drawUserModel(GuiContext context)
    {
        if (this.model == null)
        {
            return;
        }

        float partial = context.partialTicks;
        float headYaw = this.yaw;
        float headPitch = -this.pitch;

        final float factor = 1 / 16F;
        float limbSwing = this.swinging ? this.swing + partial : 0;

        if (!this.looking)
        {
            headYaw = headPitch = 0;
        }

        this.updateModel(limbSwing, headYaw, headPitch, factor, partial);

        float scale = this.getScale();

        GlStateManager.pushMatrix();
        GlStateManager.scale(model.model.scale[0], model.model.scale[1], model.model.scale[2]);
        GlStateManager.scale(-1.0F * scale, -1.0F * scale, 1.0F * scale);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);
        GlStateManager.rotate(180, 0, 1, 0);

        if (this.texture != null)
        {
            RenderCustomModel.bindLastTexture(this.texture);
        }

        this.tryPicking(context);
        this.renderModel(this.entity, headYaw, headPitch, this.timer, context.mouseX, context.mouseY, partial, factor);

        if (this.items)
        {
            renderItems(this.entity, this.model);
        }

        /* Render highlighting things on top */
        this.updateModel(limbSwing, headYaw, headPitch, factor, partial);

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();

        if (this.limb != null)
        {
            ModelCustomRenderer targetLimb = this.model.get(this.limb.name);

            if (targetLimb != null)
            {
                if (model.limbs.length > 1)
                {
                    if (targetLimb.getClass() != ModelCustomRenderer.class)
                    {
                        this.renderObjHighlight(targetLimb);
                    }
                    else
                    {
                        targetLimb.postRender(1F / 16F);
                        this.renderLimbHighlight(this.limb);
                    }
                }
                else
                {
                    if (this.origin)
                    {
                        targetLimb.postRender(1F / 16F);
                        Draw.axis(0.25F);
                    }
                }

                this.renderAnchorPreview(targetLimb);
            }
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        if (this.aabb)
        {
            this.renderAABB();
        }
    }

    protected void updateModel(float limbSwing, float headYaw, float headPitch, float factor, float partial)
    {
        this.model.materials = this.materials;
        this.model.shapes = this.shapes;
        this.model.pose = this.pose;
        this.model.swingProgress = this.swipe == -1 ? 0 : MathHelper.clamp(1.0F - (this.swipe - 1.0F * partial) / 6.0F, 0.0F, 1.0F);
        this.model.setLivingAnimations(this.entity, headYaw, headPitch, partial);
        this.model.setRotationAngles(limbSwing, this.swingAmount, this.timer, headYaw, headPitch, factor, this.entity);
    }

    protected void renderModel(EntityLivingBase dummy, float headYaw, float headPitch, int timer, int yaw, int pitch, float partial, float factor)
    {
        this.model.render(dummy, headYaw, headPitch, timer, yaw, pitch, factor);
    }

    @Override
    protected void drawForStencil(GuiContext context)
    {
        if (this.model != null)
        {
            this.model.renderForStencil(this.entity, this.swing + context.partialTicks, this.swingAmount, this.timer, this.yaw, this.pitch, 1 / 16F);
        }
    }

    @Override
    protected String getStencilValue(int value)
    {
        return this.model.limbs[value - 1].limb.name;
    }

    /**
     * Render limb highlight and the anchor and origin point of the limb 
     */
    protected void renderLimbHighlight(ModelLimb limb)
    {
        float f = 1F / 16F;
        float w = limb.size[0] * f;
        float h = limb.size[1] * f;
        float d = limb.size[2] * f;
        float o = limb.sizeOffset * f;

        float minX = 0;
        float minY = 0;
        float minZ = 0;
        float maxX = w;
        float maxY = h;
        float maxZ = d;
        float alpha = 0.2F;

        minX -= w * limb.anchor[0] + 0.1F * f;
        maxX -= w * limb.anchor[0] - 0.1F * f;
        minY -= h * limb.anchor[1] + 0.1F * f;
        maxY -= h * limb.anchor[1] - 0.1F * f;
        minZ -= d * limb.anchor[2] + 0.1F * f;
        maxZ -= d * limb.anchor[2] - 0.1F * f;

        minX *= -1;
        maxX *= -1;

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        Draw.cube(minX + o, minY - o, minZ -o, maxX - o, maxY + o, maxZ + o, 0F, 0.5F, 1F, alpha);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        if (this.origin)
        {
            Draw.axis(0.25F);
        }
    }
    
    protected void renderObjHighlight(ModelCustomRenderer renderer)
    {
        float f = 1F / 16F;
        
        GlStateManager.pushMatrix();
        
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);
        
        if (renderer.parent != null)
        {
            renderer.parent.postRender(f);
        }

        List<ModelRenderer> children = renderer.childModels;
        renderer.childModels = null;
        renderer.setupStencilRendering(1);
        renderer.render(f);
        renderer.childModels = children;

        GL11.glStencilMask(0);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, -1);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);
        
        GL11.glLoadIdentity();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GL11.glLoadIdentity();

        GlStateManager.enableBlend();
        GlStateManager.color(0F, 0.5F, 1F, 0.2F);
        
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glVertex3f(-1.0F, -1.0F, 0.0F);
        GL11.glVertex3f(1.0F, -1.0F, 0.0F);
        GL11.glVertex3f(-1.0F, 1.0F, 0.0F);
        GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        GL11.glEnd();
        GL11.glFlush();
        
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        
        GL11.glStencilMask(-1);
        GL11.glStencilFunc(GL11.GL_NEVER, 0, 0);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        renderer.postRender(f);

        if (this.origin)
        {
            Draw.axis(0.25F);
        }
    }

    /**
     * Render model's hitbox
     */
    protected void renderAABB()
    {
        ModelPose current = this.pose;

        float minX = -current.size[0] / 2.0F;
        float maxX = current.size[0] / 2.0F;
        float minY = 0.0F;
        float maxY = current.size[1];
        float minZ = -current.size[0] / 2.0F;
        float maxZ = current.size[0] / 2.0F;

        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        /* This is necessary to hid / lines which are used to reduce 
         * amount of drawing operations */
        GlStateManager.enableAlpha();

        RenderGlobal.drawBoundingBox(minX, minY, minZ, maxX, maxY, maxZ, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    protected void renderAnchorPreview(ModelCustomRenderer renderer)
    {
        if (this.anchorPreview != null && renderer.min != null && renderer.max != null)
        {
            float ax = (float) this.anchorPreview.vector.a.value;
            float ay = (float) this.anchorPreview.vector.b.value;
            float az = (float) this.anchorPreview.vector.c.value;

            float dx = renderer.max.x - renderer.min.x;
            float dy = renderer.max.y - renderer.min.y;
            float dz = renderer.max.z - renderer.min.z;

            float x = renderer.min.x + Interpolations.lerp(0, dx, ax) - this.limb.origin[0];
            float y = renderer.min.y + Interpolations.lerp(0, dy, ay) - this.limb.origin[1];
            float z = renderer.min.z + Interpolations.lerp(0, dz, az) - this.limb.origin[2];

            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();

            GlStateManager.pushMatrix();
            GlStateManager.translate(-x, -y, z);

            Draw.point(0, 0, 0);

            GlStateManager.popMatrix();

            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
        }
    }

    public void setPose(ModelPose pose)
    {
        this.setPose(pose, pose == null ? null : pose.shapes);
    }

    public void setPose(ModelPose pose, List<ShapeKey> shapes)
    {
        this.pose = pose;
        this.shapes = shapes;
    }
}