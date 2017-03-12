package mchorse.blockbuster.model_editor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mchorse.blockbuster.client.gui.utils.GuiUtils;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.model_editor.elements.GuiLimbEditor;
import mchorse.blockbuster.model_editor.elements.GuiLimbsList;
import mchorse.blockbuster.model_editor.elements.GuiLimbsList.ILimbPicker;
import mchorse.blockbuster.model_editor.elements.GuiListViewer;
import mchorse.blockbuster.model_editor.elements.GuiListViewer.IListResponder;
import mchorse.blockbuster.model_editor.elements.GuiModelModal;
import mchorse.blockbuster.model_editor.modal.GuiInputModal;
import mchorse.blockbuster.model_editor.modal.GuiModal;
import mchorse.blockbuster.model_editor.modal.IModalCallback;
import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.api.models.Model.Limb;
import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.client.model.parsing.ModelParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Model editor GUI
 *
 * This GUI is responsible providing to player tools to edit custom models,
 * just like McME, but better and up to date.
 */
public class GuiModelEditor extends GuiScreen implements IModalCallback, IListResponder, ILimbPicker
{
    /* Field IDs */
    public static final int CHANGE_NAME = -10;
    public static final int CHANGE_PARENT = -11;
    public static final int ADD_LIMB = -20;
    public static final int MODEL_PROPS = -30;
    public static final int SAVE = -40;
    public static final int NEW = -50;

    /* Data */

    /**
     * Currently data model which we are editing
     */
    public Model data;

    /**
     * Compiled data model which we are currently editing
     */
    private ModelCustom model;

    /**
     * Cached texture path
     */
    private ResourceLocation textureRL;

    /**
     * Model name for saving
     */
    private String modelName = "";

    /* GUI fields */

    /**
     * Current modal
     */
    private GuiModal currentModal;

    /**
     * Available poses
     */
    private GuiListViewer poses;

    /**
     * Limbs sidebar
     */
    private GuiLimbsList limbs;

    /**
     * Limb editor
     */
    private GuiLimbEditor limbEditor;

    /**
     * Texture path field
     */
    private GuiTextField texture;

    /**
     * Pose field
     */
    private GuiButton pose;

    /**
     * Save button, this will prompt user to choose a name
     */
    private GuiButton save;

    /**
     * Create clean, new, model out of existing ones or
     */
    private GuiButton clean;

    /**
     * Button which returns user back to main menu
     */
    private GuiButton back;

    /**
     * Add a limb
     */
    private GuiTextureButton addLimb;

    /**
     * Remove current limb
     */
    private GuiTextureButton removeLimb;

    /**
     * Edit model properties button
     */
    private GuiButton edit;

    /**
     * Ticks timer for arm idling animation
     */
    private int timer;

    /**
     * Model's scale
     */
    private float scale;

    /* Mouse dragging */
    private boolean dragging;
    private int prevX;
    private int prevY;

    /* Model spinning */
    private float yaw;
    private float pitch;

    private float prevYaw;
    private float prevPitch;

    /* Main menu flag */
    private boolean mainMenu;

    /**
     * Setup by default
     */
    public GuiModelEditor(boolean mainMenu)
    {
        this.mainMenu = mainMenu;

        this.poses = new GuiListViewer(null, this);
        this.limbs = new GuiLimbsList(this);
        this.limbEditor = new GuiLimbEditor(this);

        this.setupModel(ModelCustom.MODELS.get("blockbuster.steve"));
        this.textureRL = new ResourceLocation("blockbuster:textures/entity/actor.png");
    }

    /**
     * Open the modal
     */
    public void openModal(GuiModal modal)
    {
        this.currentModal = modal;
        modal.initiate();
    }

    /**
     * Setup the model
     */
    private void setupModel(ModelCustom model)
    {
        this.data = ModelUtils.cloneModel(model.model);

        List<String> poses = new ArrayList<String>();
        poses.addAll(this.data.poses.keySet());

        this.poses.setStrings(poses);
        this.limbs.setModel(this.data);

        this.model = this.buildModel();
        this.changePose("standing");
    }

    /**
     * Build the model from data model
     */
    public ModelCustom buildModel()
    {
        try
        {
            return new ModelParser().parseModel(this.data, ModelCustom.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Build the model from data model
     */
    public void rebuildModel()
    {
        Model.Pose oldPose = this.model.pose;

        this.model = this.buildModel();

        if (this.model != null)
        {
            this.model.pose = oldPose;
        }
    }

    /**
     * Change pose
     */
    private void changePose(String pose)
    {
        this.model.pose = this.data.getPose(pose);

        if (this.pose != null)
        {
            this.pose.displayString = pose;
        }

        this.limbEditor.setPose(this.model.pose);
    }

    /**
     * Set texture
     */
    private void setTexture(String texture)
    {
        this.textureRL = new ResourceLocation(texture);

        if (this.texture != null)
        {
            this.texture.setText(this.textureRL.toString());
        }
    }

    /* Initiate GUI and handle input from other widgets */

    @Override
    public void initGui()
    {
        /* Initiate the texture field */
        this.texture = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 49, this.height - 24, 98, 18);
        this.texture.setMaxStringLength(400);
        this.texture.setText(this.textureRL.toString());

        /* Buttons */
        this.edit = new GuiTextureButton(5, this.width - 107, 5, GuiLimbEditor.GUI).setTexPos(48, 32).setActiveTexPos(48, 48);
        this.save = new GuiTextureButton(0, this.width - 79, 5, GuiLimbEditor.GUI).setTexPos(0, 32).setActiveTexPos(0, 48);
        this.clean = new GuiTextureButton(1, this.width - 52, 5, GuiLimbEditor.GUI).setTexPos(16, 32).setActiveTexPos(16, 48);
        this.back = new GuiTextureButton(-100, this.width - 25, 5, GuiLimbEditor.GUI).setTexPos(32, 32).setActiveTexPos(32, 48);

        this.pose = new GuiButton(2, this.width - 110, this.height - 25, 100, 20, "standing");

        this.addLimb = new GuiTextureButton(3, this.width - 25, 30, GuiLimbEditor.GUI).setTexPos(16, 0).setActiveTexPos(16, 16);
        this.removeLimb = new GuiTextureButton(4, this.width - 25 - 16, 30, GuiLimbEditor.GUI).setTexPos(32, 0).setActiveTexPos(32, 16);

        this.buttonList.add(this.save);
        this.buttonList.add(this.clean);
        this.buttonList.add(this.edit);
        this.buttonList.add(this.back);

        this.buttonList.add(this.pose);

        this.buttonList.add(this.addLimb);
        this.buttonList.add(this.removeLimb);

        this.poses.updateRect(this.width - 110, this.height - 106, 100, 80);
        this.poses.setHidden(true);

        this.limbEditor.initiate(10, 47);
        this.limbs.updateRect(this.width - 111, 47, 102, this.height - 47 - 30);

        if (this.currentModal != null)
        {
            this.currentModal.initiate();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            GuiInputModal modal = new GuiInputModal(SAVE, this, this.fontRendererObj);

            modal.label = "In order to save, you should specify a name of your model:";
            modal.setInput(this.modelName);

            this.openModal(modal);
        }
        else if (button.id == 1)
        {
            GuiInputModal modal = new GuiInputModal(NEW, this, this.fontRendererObj);

            modal.label = "Type in a model which you want to use for ";
            modal.setInput("blockbuster.steve");

            this.openModal(modal);
        }
        else if (button.id == 2)
        {
            this.poses.setHidden(false);
        }
        else if (button.id == 3)
        {
            GuiInputModal modal = new GuiInputModal(ADD_LIMB, this, this.fontRendererObj);

            modal.label = "Choose a name for your new limb:";

            this.openModal(modal);
        }
        else if (button.id == 4)
        {
            this.limbs.removeLimb();
            this.rebuildModel();
        }
        else if (button.id == 5)
        {
            GuiModelModal modal = new GuiModelModal(MODEL_PROPS, this, this.fontRendererObj);

            modal.label = "This modal provides a way to edit model's general properties.";
            modal.model = this.data;

            this.openModal(modal);
        }
        else if (button == this.back)
        {
            this.mc.displayGuiScreen(this.mainMenu ? new GuiMainMenu() : null);
        }
    }

    /**
     * It's like {@link #actionPerformed(GuiButton)}, but only comes from modal
     * windows.
     */
    @Override
    public void modalButtonPressed(GuiModal modal, GuiButton button)
    {
        if (button.id == CHANGE_NAME)
        {
            this.limbEditor.changeName(((GuiInputModal) modal).getInput());
        }
        else if (button.id == CHANGE_PARENT)
        {
            this.limbEditor.changeParent(((GuiInputModal) modal).getInput());
            this.rebuildModel();
        }
        else if (button.id == ADD_LIMB)
        {
            this.limbs.addLimb(((GuiInputModal) modal).getInput());
            this.rebuildModel();
        }
        else if (button.id == MODEL_PROPS)
        {
            try
            {
                GuiModelModal model = (GuiModelModal) modal;

                String name = model.name.getText();
                float[] scale = new float[] {Float.parseFloat(model.scale.a.getText()), Float.parseFloat(model.scale.b.getText()), Float.parseFloat(model.scale.c.getText())};
                int[] texture = new int[] {Integer.parseInt(model.texture.a.getText()), Integer.parseInt(model.texture.b.getText())};

                if (name.isEmpty() || scale[0] <= 0 || scale[1] <= 0 || scale[2] <= 0 || texture[0] <= 0 || texture[1] <= 0)
                {
                    return;
                }

                this.data.name = name;
                this.data.scale = scale;
                this.data.texture = texture;
                this.rebuildModel();
            }
            catch (Exception e)
            {}
        }
        else if (button.id == SAVE)
        {
            String name = ((GuiInputModal) modal).getInput();

            if (name.isEmpty())
            {
                return;
            }

            File folder = new File(ClientProxy.config, "models/" + name);
            File file = new File(folder, "model.json");

            folder.mkdirs();

            Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
            String output = gson.toJson(this.data);

            try
            {
                PrintWriter writer = new PrintWriter(file);

                writer.print(output);
                writer.close();

                this.modelName = name;
                ModelCustom.MODELS.put("blockbuster." + name, this.buildModel());
            }
            catch (Exception e)
            {
                return;
            }
        }
        else if (button.id == NEW)
        {
            String name = ((GuiInputModal) modal).getInput();

            if (!ModelCustom.MODELS.containsKey(name))
            {
                return;
            }

            this.setupModel(ModelCustom.MODELS.get(name));
            this.modelName = name;
        }

        this.currentModal = null;
    }

    @Override
    public void pickedValue(String value)
    {
        this.changePose(value);
    }

    @Override
    public void pickLimb(Limb limb)
    {
        this.limbEditor.setLimb(limb);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.poses.setWorldAndResolution(mc, width, height);
        this.limbs.setWorldAndResolution(mc, width, height);
    }

    /* Handling input */

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();

        this.limbs.handleKeyboardInput();
    }

    /**
     * Key pressed
     *
     * Used for delegating keyboard events to different fields and to current
     * model (if it's active).
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!this.mainMenu && this.currentModal == null)
        {
            super.keyTyped(typedChar, keyCode);
        }

        if (this.currentModal == null)
        {
            this.texture.textboxKeyTyped(typedChar, keyCode);

            if (this.texture.isFocused() && !this.texture.getText().equals(this.textureRL.toString()))
            {
                this.textureRL = new ResourceLocation(this.texture.getText());
            }

            this.limbEditor.keyTyped(typedChar, keyCode);
        }
        else
        {
            this.currentModal.keyTyped(typedChar, keyCode);

            if (keyCode == 1)
            {
                this.currentModal = null;
            }
        }
    }

    /**
     * Handle mouse input
     *
     * This method is used to prevent delegation of mouse input events in the
     * limbs list (when poses list overlays limbs list) and zooming the model.
     */
    @Override
    public void handleMouseInput() throws IOException
    {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        super.handleMouseInput();

        /* Zooming the model */
        if (x > 120 && x < this.width - 120)
        {
            int scroll = -Mouse.getEventDWheel();

            if (scroll != 0)
            {
                this.scale += Math.copySign(2.0, scroll);
                this.scale = MathHelper.clamp_float(this.scale, -100, 500);
            }
        }

        if (!this.poses.isInside(x, y))
        {
            this.limbs.handleMouseInput();
        }

        this.poses.handleMouseInput();
    }

    /**
     * Mouse clicked
     *
     * This method is used for responsible for delegating mouse click event to
     * the child widgets, modal and to rotation thing.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.poses.isInside(mouseX, mouseY))
        {
            return;
        }

        if (this.currentModal == null)
        {
            this.texture.mouseClicked(mouseX, mouseY, mouseButton);
            this.limbEditor.mouseClicked(mouseX, mouseY, mouseButton);

            if (mouseX > 120 && mouseX < this.width - 120)
            {
                this.dragging = true;
                this.prevX = mouseX;
                this.prevY = mouseY;
            }

            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        else
        {
            this.currentModal.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Mouse released
     *
     * Used to calculate final yaw and pitch for the model rotation.
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.dragging)
        {
            this.dragging = false;
            this.prevYaw += this.yaw;
            this.prevPitch += this.pitch;
            this.yaw = this.pitch = 0;
            this.prevX = this.prevY = 0;
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /* Updating and rendering */

    @Override
    public void updateScreen()
    {
        this.timer++;
    }

    /**
     * Draw the screen
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        GuiUtils.drawHorizontalGradientRect(0, 0, 120, this.height, 0x55000000, 0x00000000, this.zLevel);
        GuiUtils.drawHorizontalGradientRect(this.width - 120, 0, this.width, this.height, 0x00000000, 0x55000000, this.zLevel);

        /* Draw the model */
        float scale = this.height / 3;
        float x = this.width / 2;
        float y = this.height / 2;
        float yaw = (x - mouseX) / this.width * 90;
        float pitch = (y + scale + mouseY) / this.height * 90 - 135;

        try
        {
            this.drawModel(x, y, scale + this.scale, yaw, pitch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /* Labels */
        this.fontRendererObj.drawStringWithShadow("Model Editor", 10, 10, 0xffffff);
        this.fontRendererObj.drawStringWithShadow("Limbs", this.width - 105, 35, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.limbEditor.draw(mouseX, mouseY, partialTicks);
        this.limbs.drawScreen(mouseX, mouseY, partialTicks);
        this.poses.drawScreen(mouseX, mouseY, partialTicks);

        if (this.dragging)
        {
            this.yaw = -(this.prevX - mouseX);
            this.pitch = this.prevY - mouseY;
        }

        this.texture.drawTextBox();

        /* Draw current modal */
        if (this.currentModal != null)
        {
            Gui.drawRect(0, 0, this.width, this.height, 0x55000000);
            this.currentModal.drawModal(mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Draw currently edited model
     *
     * Totally stole it from Metamorph's code from {@link GuiUtils}.
     */
    private void drawModel(float x, float y, float scale, float yaw, float pitch)
    {
        /* Extending model's rendering range, otherwise it gets clipped */
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, this.width, this.height, 0.0D, 1000.0D, 3000000.0D);
        GlStateManager.matrixMode(5888);

        EntityPlayer player = this.mc.thePlayer;
        float factor = 0.0625F;

        this.mc.renderEngine.bindTexture(this.textureRL);

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F + this.prevYaw + this.yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.prevPitch + this.pitch, 1.0F, 0.0F, 0.0F);

        pitch += this.prevPitch + this.pitch;

        RenderHelper.enableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        this.model.setLivingAnimations(player, 0, 0, 0);
        this.model.setRotationAngles(0, 0, this.timer, yaw, pitch, factor, player);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, this.model.pose.size[1] / 2, 0);
        this.model.render(player, 0, 0, 0, 0, 0, factor);
        GlStateManager.popMatrix();

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
    }
}