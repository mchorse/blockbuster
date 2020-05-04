package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ItemRenderer;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.mclib.client.Draw;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.utils.DummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Map;

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
    public ModelPose pose;
    public ModelLimb limb;

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
        float partial = context.partialTicks;
        float headYaw = this.yaw;
        float headPitch = -this.pitch;

        final float factor = 1 / 16F;
        float limbSwing = this.swing + partial;

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

        this.renderModel(this.entity, headYaw, headPitch, this.timer, context.mouseX, context.mouseY, partial, factor);
        this.tryPicking(context);

        if (this.items)
        {
            ItemRenderer.renderItems(this.entity, this.model, limbSwing, this.swingAmount, partial, this.timer, context.mouseX, context.mouseY, factor);
        }

        /* Render highlighting things on top */
        this.updateModel(limbSwing, headYaw, headPitch, factor, partial);

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();

        if (this.limb != null)
        {
            for (ModelCustomRenderer limb : model.limbs)
            {
                if (limb.limb.name.equals(this.limb.name))
                {
                    limb.postRender(1F / 16F);

                    break;
                }
            }

            this.renderLimbHighlight(this.limb);
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
        this.model.renderForStencil(this.entity, this.swing + context.partialTicks, this.swingAmount, this.timer, this.yaw, this.pitch, 1 / 16F);
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
}