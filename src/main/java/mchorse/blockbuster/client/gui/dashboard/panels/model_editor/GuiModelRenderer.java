package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.model_editor.DummyEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;

public class GuiModelRenderer extends GuiElement
{
    public GuiModelEditorPanel panel;

    public DummyEntity dummy;
    private IBlockState block = Blocks.GRASS.getDefaultState();

    private boolean swinging;
    private float swing;
    private float swingAmount;
    private float scale;
    private int swipe;
    private int timer;

    private boolean dragging;
    private float yaw;
    private float pitch;

    private float lastX;
    private float lastY;

    public GuiModelRenderer(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc);

        this.panel = panel;
        this.dummy = new DummyEntity(null);
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
            this.yaw -= this.lastX - mouseX;
            this.pitch += this.lastY - mouseY;
        }

        this.dragging = false;

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
        ModelCustom model = this.panel.renderModel;

        /* Changing projection mode to perspective. In order for this to 
         * work, depth buffer must also be cleared. Thanks to Gegy for 
         * pointing this out (depth buffer)! */
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        Project.gluPerspective(70, (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, 1000);
        GlStateManager.matrixMode(5888);

        Model.Limb limb = null;

        float factor = 0.0625F;

        float newYaw = this.yaw;
        float newPitch = this.pitch;

        if (this.dragging)
        {
            newYaw -= this.lastX - yaw;
            newPitch += this.lastY - pitch;
        }

        RenderHelper.enableStandardItemLighting();

        GlStateManager.enableColorMaterial();
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0, -1, -2 - this.scale);
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F + newYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(newPitch, 1.0F, 0.0F, 0.0F);

        this.renderGround();

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        float limbSwing = this.swing + partialTicks;

        model.pose = this.panel.pose;
        model.setLivingAnimations(this.dummy, 0, 0, partialTicks);
        model.setRotationAngles(limbSwing, this.swingAmount, this.timer, 0, 0, factor, this.dummy);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.scale(model.model.scale[0], model.model.scale[1], model.model.scale[2]);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        if (this.panel.renderTexture != null)
        {
            this.mc.renderEngine.bindTexture(this.panel.renderTexture);
        }

        model.render(this.dummy, 0, 0, this.timer, yaw, pitch, factor);

        /*
        if (this.items)
        {
            ItemRenderer.renderItems(this.dummy, this.model, limbSwing, this.swingAmount, ticks, this.timer, yaw, pitch, factor);
        }
        */

        GlStateManager.popMatrix();

        /*
        if (this.aabb)
        {
            this.renderAABB();
        }
        */

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

    public void renderGround()
    {
        Minecraft mc = Minecraft.getMinecraft();

        BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -0.5F, 0);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(this.block, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}