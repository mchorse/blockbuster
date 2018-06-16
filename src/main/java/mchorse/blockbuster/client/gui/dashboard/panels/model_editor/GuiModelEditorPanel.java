package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.io.File;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.api.Model.Pose;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiModelEditorPanel extends GuiDashboardPanel
{
    /* GUI stuff */
    private GuiButtonElement<GuiTextureButton> openModel;
    private GuiStringListElement modelList;
    private GuiStringListElement limbList;

    /* Current data */
    private Model model;
    private Pose pose;
    private Limb limb;

    private DummyEntity dummy;
    private ModelCustom renderModel;
    private ResourceLocation texture;
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

    public GuiModelEditorPanel(Minecraft mc)
    {
        super(mc);

        this.dummy = new DummyEntity(null);

        this.modelList = new GuiStringListElement(mc, (str) -> this.setModel(str));
        this.modelList.resizer().set(0, 20, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE);
        this.modelList.add(ModelCustom.MODELS.keySet());
        this.children.add(this.modelList);

        this.limbList = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbList.resizer().set(0, 20, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE);
        this.limbList.resizer().x.set(1, Measure.RELATIVE, -80);
        this.children.add(this.limbList);

        this.setModel("steve");
    }

    public void setModel(String name)
    {
        ModelCustom model = ModelCustom.MODELS.get(name);

        if (model != null)
        {
            this.renderModel = model;
            this.model = this.renderModel.model;
            this.renderModel.pose = this.model.getPose("standing");
            this.pose = this.renderModel.pose;
            this.texture = this.model.defaultTexture;

            if (this.texture == null)
            {
                Map<String, File> skins = Blockbuster.proxy.models.pack.skins.get(name);

                if (skins != null && !skins.isEmpty())
                {
                    this.texture = new ResourceLocation("blockbuster.actors", name + "/" + skins.keySet().iterator().next());
                }
            }

            if (this.texture == null)
            {
                this.texture = new ResourceLocation("blockbuster", "textures/entity/actor.png");
            }

            this.limbList.clear();
            this.limbList.add(this.model.limbs.keySet());
        }
    }

    public void setLimb(String str)
    {
        Limb limb = this.model.limbs.get(str);

        if (limb != null)
        {
            this.limb = limb;
        }
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

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.update(mouseX, mouseY);
        this.drawModel(partialTicks, mouseX, mouseY, 0);

        this.limbList.setVisible(false);
        this.modelList.setVisible(false);

        super.draw(mouseX, mouseY, partialTicks);
    }

    /**
     * Update logic
     */
    private void update(int x, int y)
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
    private void drawModel(float scale, float yaw, float pitch, float ticks)
    {
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
        float oldSwing = this.renderModel.swingProgress;

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

        // pitch += newPitch;

        this.renderGround();

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        float limbSwing = this.swing + ticks;

        this.renderModel.pose = this.pose;
        this.renderModel.setLivingAnimations(this.dummy, 0, 0, ticks);
        this.renderModel.setRotationAngles(limbSwing, this.swingAmount, this.timer, 0, 0, factor, this.dummy);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        /* TODO: bring back scale */
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        if (this.texture != null)
        {
            this.mc.renderEngine.bindTexture(this.texture);
        }

        this.renderModel.render(this.dummy, 0, 0, this.timer, yaw, pitch, factor);

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