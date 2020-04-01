package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.Consumer;

import mchorse.blockbuster.utils.April;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ItemRenderer;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.utils.DummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Model renderer which renders Blockbuster models 
 */
public class GuiBBModelRenderer extends GuiModelRenderer
{
    public static final ResourceLocation PIXEL = new ResourceLocation("blockbuster", "textures/pixel.png");

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

    private boolean tryPicking;
    public Consumer<String> pickingCallback;

    public static void drawCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        drawCube(buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawCube(VertexBuffer buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        /* Top */
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        /* Bottom */
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        /* Left */
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        /* Right */
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        /* Front */
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();

        /* Back */
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
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
        this.dummy.toggleItems(this.items);
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
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.dragging && GuiScreen.isCtrlKeyDown())
        {
            this.tryPicking = true;
            this.dragging = false;
        }

        return result;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.tryPicking = false;
    }

    protected float getScale()
    {
        return 1;
    }

    @Override
    protected void drawModel(float headYaw, float headPitch, int mouseX, int mouseY, float partial)
    {
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

        if (this.texture != null)
        {
            RenderCustomModel.bindLastTexture(this.texture);
        }

        this.renderModel(this.dummy, headYaw, headPitch, this.timer, mouseX, mouseY, partial, factor);

        if (this.items)
        {
            ItemRenderer.renderItems(this.dummy, this.model, limbSwing, this.swingAmount, partial, this.timer, mouseX, mouseY, factor);
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
        this.model.setLivingAnimations(this.dummy, headYaw, headPitch, partial);
        this.model.setRotationAngles(limbSwing, this.swingAmount, this.timer, headYaw, headPitch, factor, this.dummy);
    }

    protected void renderModel(DummyEntity dummy, float headYaw, float headPitch, int timer, int yaw, int pitch, float partial, float factor)
    {
        this.model.render(dummy, headYaw, headPitch, timer, yaw, pitch, factor);

        if (this.tryPicking)
        {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            int scale = Minecraft.getMinecraft().displayWidth / screen.width;
            int x = yaw * scale;
            int y = Minecraft.getMinecraft().displayHeight - pitch * scale - 1;

            GL11.glClearStencil(0);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

            this.model.renderForStencil(dummy, headYaw, headPitch, timer, yaw, pitch, factor);

            ByteBuffer buffer = ByteBuffer.allocateDirect(1);
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_STENCIL_INDEX, GL11.GL_UNSIGNED_BYTE, buffer);

            buffer.rewind();

            if (this.pickingCallback != null)
            {
                int value = buffer.get();

                if (value > 0)
                {
                    this.pickingCallback.accept(this.model.limbs[value - 1].limb.name);
                }
            }

            this.tryPicking = false;
        }
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

        int color = April.aprilColor("dasdq211");

        drawCube(minX + o, minY - o, minZ -o, maxX - o, maxY + o, maxZ + o, (color >> 16 & 0xff) / 255F, (color >> 8 & 0xff) / 255F, (color & 0xff) / 255F, alpha);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        if (this.origin)
        {
            GL11.glLineWidth(5);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0.25, 0, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0, 0.25, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0.25);
            GL11.glEnd();

            GL11.glLineWidth(3);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor3d(1, 0, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0.25, 0, 0);

            GL11.glColor3d(0, 1, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0, 0.25, 0);

            GL11.glColor3d(0, 0, 1);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0.25);
            GL11.glEnd();
            GL11.glLineWidth(1);

            GL11.glPointSize(12);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glColor3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glEnd();

            GL11.glPointSize(10);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glColor3d(1, 1, 1);
            GL11.glVertex3d(0, 0, 0);
            GL11.glEnd();
            GL11.glPointSize(1);
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