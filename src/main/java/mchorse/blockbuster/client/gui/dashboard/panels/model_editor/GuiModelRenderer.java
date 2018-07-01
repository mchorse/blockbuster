package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.DummyEntity;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ItemRenderer;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiModelRenderer extends GuiElement
{
    public DummyEntity dummy;
    private IBlockState block = Blocks.GRASS.getDefaultState();

    public boolean swinging;
    private float swing;
    private float swingAmount;
    private float scale;
    private int swipe;
    private int timer;

    private boolean dragging;
    private boolean position;
    private float yaw;
    private float pitch;
    private float x;
    private float y;

    private float lastX;
    private float lastY;

    public boolean items;
    public boolean aabb;
    public boolean looking = true;

    public ResourceLocation texture;
    public ModelCustom model;
    public ModelPose pose;
    public ModelLimb limb;

    public GuiModelRenderer(Minecraft mc)
    {
        super(mc);

        this.dummy = new DummyEntity(null);
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

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        this.dragging = true;
        this.lastX = mouseX;
        this.lastY = mouseY;

        this.position = GuiScreen.isShiftKeyDown() || mouseButton == 2;

        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        if (super.mouseScrolled(mouseX, mouseY, scroll))
        {
            return true;
        }

        this.scale += Math.copySign(0.25F, scroll);
        this.scale = MathHelper.clamp_float(this.scale, -1.5F, 30);

        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.dragging)
        {
            if (this.position)
            {
                this.x -= (this.lastX - mouseX) / 60;
                this.y += (this.lastY - mouseY) / 60;
            }
            else
            {
                this.yaw -= this.lastX - mouseX;
                this.pitch += this.lastY - mouseY;
            }
        }

        this.dragging = false;
        this.position = false;

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);
        this.update();

        this.drawModel(mouseX, mouseY, partialTicks);
    }

    /**
     * Update logic
     */
    private void update()
    {
        this.timer = this.mc.thePlayer != null ? this.mc.thePlayer.ticksExisted : this.timer + 1;
        this.dummy.ticksExisted = this.timer;

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

    /**
     * Draw currently edited model
     */
    private void drawModel(int yaw, int pitch, float partialTicks)
    {
        ModelCustom model = this.model;

        /* Changing projection mode to perspective. In order for this to 
         * work, depth buffer must also be cleared. Thanks to Gegy for 
         * pointing this out (depth buffer)! */
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        Project.gluPerspective(70, (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, 1000);
        GlStateManager.matrixMode(5888);

        float factor = 0.0625F;

        float newYaw = this.yaw;
        float newPitch = this.pitch;

        float x = this.x;
        float y = this.y;

        if (this.dragging)
        {
            if (this.position)
            {
                x -= (this.lastX - yaw) / 60;
                y += (this.lastY - pitch) / 60;
            }
            else
            {
                newYaw -= this.lastX - yaw;
                newPitch += this.lastY - pitch;
            }
        }

        RenderHelper.enableStandardItemLighting();

        GlStateManager.enableColorMaterial();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0 + x, -1 + y, -2 - this.scale);
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F + newYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(newPitch, 1.0F, 0.0F, 0.0F);

        this.renderGround();

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        float limbSwing = this.swing + partialTicks;

        float headYaw = newYaw;
        float headPitch = newPitch;

        if (!this.looking)
        {
            headYaw = headPitch = 0;
        }

        model.pose = this.pose;
        model.swingProgress = this.swipe == -1 ? 0 : MathHelper.clamp_float(1.0F - (this.swipe - 1.0F * partialTicks) / 6.0F, 0.0F, 1.0F);
        model.setLivingAnimations(this.dummy, headYaw, headPitch, partialTicks);
        model.setRotationAngles(limbSwing, this.swingAmount, this.timer, headYaw, headPitch, factor, this.dummy);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.scale(model.model.scale[0], model.model.scale[1], model.model.scale[2]);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        if (this.texture != null)
        {
            RenderCustomModel.bindLastTexture(this.texture);
        }

        model.render(this.dummy, headYaw, headPitch, this.timer, yaw, pitch, factor);

        if (this.items)
        {
            ItemRenderer.renderItems(this.dummy, this.model, limbSwing, this.swingAmount, partialTicks, this.timer, yaw, pitch, factor);
        }

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
                }
            }

            this.renderLimbHighlight(this.limb);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();

        if (this.aabb)
        {
            this.renderAABB();
        }

        GlStateManager.disableDepth();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        /* Return back to orthographic projection */
        GuiScreen screen = this.mc.currentScreen;

        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, screen.width, screen.height, 0.0D, 1000.0D, 3000000.0D);
        GlStateManager.matrixMode(5888);
    }

    /**
     * Render limb highlight and the anchor and origin point of the limb 
     */
    public void renderLimbHighlight(ModelLimb limb)
    {
        float f = 1F / 16F;
        float w = limb.size[0] * f;
        float h = limb.size[1] * f;
        float d = limb.size[2] * f;

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

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        /* Top */
        buffer.pos(minX, maxY, minZ).color(0, 0.6F, 1, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(0, 0.6F, 1, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(0, 0.6F, 1, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(0, 0.6F, 1, alpha).endVertex();

        /* Bottom */
        buffer.pos(minX, minY, minZ).color(0, 0.4F, 1, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(0, 0.4F, 1, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(0, 0.4F, 1, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(0, 0.4F, 1, alpha).endVertex();

        /* Left */
        buffer.pos(minX, maxY, minZ).color(0.2F, 0.5F, 1, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(0.2F, 0.5F, 1, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(0.2F, 0.5F, 1, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(0, 0.5F, 1, alpha).endVertex();

        /* Right */
        buffer.pos(maxX, maxY, minZ).color(0, 0.5F, 1, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(0, 0.5F, 1, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(0, 0.5F, 1, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(0, 0.5F, 1, alpha).endVertex();

        /* Front */
        buffer.pos(minX, maxY, minZ).color(0, 0.5F, 1, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(0, 0.5F, 1, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(0, 0.5F, 1, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(0, 0.5F, 1, alpha).endVertex();

        /* Back */
        buffer.pos(minX, maxY, maxZ).color(0, 0.5F, 0.8F, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(0, 0.5F, 0.8F, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(0, 0.5F, 0.8F, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(0, 0.5F, 0.8F, alpha).endVertex();
        tessellator.draw();

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        if (this.aabb)
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
    private void renderAABB()
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

    /**
     * Render block of grass under the model (which signify where 
     * located the ground below the model) 
     */
    public void renderGround()
    {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.enableDepth();
        BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -0.5F, 0);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(this.block, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }
}