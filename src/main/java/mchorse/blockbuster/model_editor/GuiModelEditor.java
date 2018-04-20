package mchorse.blockbuster.model_editor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.client.gui.utils.GuiUtils;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.model_editor.elements.GuiLimbEditor;
import mchorse.blockbuster.model_editor.elements.GuiLimbsList;
import mchorse.blockbuster.model_editor.elements.GuiLimbsList.ILimbPicker;
import mchorse.blockbuster.model_editor.elements.GuiTexturePicker;
import mchorse.blockbuster.model_editor.elements.GuiTexturePicker.ITexturePicker;
import mchorse.blockbuster.model_editor.elements.modals.GuiAlertModal;
import mchorse.blockbuster.model_editor.elements.modals.GuiInputModal;
import mchorse.blockbuster.model_editor.elements.modals.GuiModelModal;
import mchorse.blockbuster.model_editor.elements.modals.GuiNewModal;
import mchorse.blockbuster.model_editor.elements.modals.GuiParentModal;
import mchorse.blockbuster.model_editor.elements.modals.GuiPoseModal;
import mchorse.blockbuster.model_editor.elements.scrolls.GuiModelsView.ModelCell;
import mchorse.blockbuster.model_editor.modal.GuiModal;
import mchorse.blockbuster.model_editor.modal.IModalCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Model editor GUI
 *
 * This GUI is responsible providing to player tools to edit custom models,
 * just like McME, but better and up to date.
 */
public class GuiModelEditor extends GuiScreen implements IModalCallback, ILimbPicker, ITexturePicker
{
    private final String strME = I18n.format("blockbuster.gui.me.model_editor");
    private final String strLimbs = I18n.format("blockbuster.gui.me.limbs");

    /* Field IDs */
    public static final int CHANGE_NAME = -10;
    public static final int CHANGE_PARENT = -11;
    public static final int ADD_LIMB = -20;
    public static final int MODEL_PROPS = -30;
    public static final int SAVE = -40;
    public static final int NEW = -50;
    public static final int ADD_POSE = -60;
    public static final int SELECT_POSE = -61;
    public static final int REMOVE_POSE = -62;

    /* Data */

    /**
     * Currently data model which we are editing
     */
    public Model data;

    /**
     * Dummy entity for rendering
     */
    public EntityLivingBase dummy;

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
     * Limbs sidebar
     */
    private GuiLimbsList limbs;

    /**
     * Limb editor
     */
    private GuiLimbEditor limbEditor;

    /**
     * Texture picker
     */
    private GuiTexturePicker texturePicker;

    /**
     * Texture path field
     */
    private GuiButton showTextures;

    /**
     * Pose button which opens up poses modal
     */
    private GuiButton pose;

    /**
     * Save button, this will prompt user to choose a name
     */
    private GuiButton save;

    /**
     * Create clean, new, model out of existing ones or based on built-in model
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
     * Edit model general properties button
     */
    private GuiButton edit;

    /**
     * Swipe button
     */
    private GuiButton swipeArms;

    /**
     * Swing button
     */
    private GuiButton swingLegs;

    /**
     * Whether to render items
     */
    private GuiButton renderItems;

    /**
     * Whether to render AABB
     */
    private GuiButton renderAABB;

    /* Mouse dragging and model rotation */
    private boolean dragging;
    private int prevX;
    private int prevY;

    private float yaw;
    private float pitch;

    private float prevYaw;
    private float prevPitch;

    /* Model gameplay feature variables */

    /**
     * Idle timer (basically for age in ticks argument)
     */
    private int timer;

    /**
     * Model's scale
     */
    private float scale;

    /**
     * Swipe animation (0-6 is animating, -1 is turned off)
     */
    private int swipe = -1;

    /**
     * Whether character's limbs are swinging
     */
    private boolean swinging;

    private float swingAmount;
    private float swing;

    /**
     * Render items in the limbs which are responsible for holding items
     */
    private boolean items;

    /**
     * Render hit box around the model based on its pose size
     */
    private boolean aabb;

    /**
     * Whether player opened this menu from main menu or from the game
     */
    private boolean mainMenu;

    public GuiModelEditor(boolean mainMenu)
    {
        this.mainMenu = mainMenu;

        this.limbs = new GuiLimbsList(this);
        this.limbEditor = new GuiLimbEditor(this);
        this.texturePicker = new GuiTexturePicker(this, Blockbuster.proxy.models.pack);
        this.dummy = new DummyEntity(null);

        this.modelName = "steve";
        this.setupModel(ModelCustom.MODELS.get("steve"));
        this.setTexture("blockbuster:textures/entity/actor.png");
    }

    /**
     * Setup the model
     */
    private void setupModel(ModelCustom model)
    {
        this.data = model.model.clone();

        /* TODO: Move to Metamorph ASAP */
        this.data.providesObj = model.model.providesObj;

        for (Map.Entry<String, Model.Limb> limb : model.model.limbs.entrySet())
        {
            this.data.limbs.get(limb.getKey()).origin = limb.getValue().origin;
        }

        List<String> poses = new ArrayList<String>();
        poses.addAll(this.data.poses.keySet());

        this.limbs.reset();
        this.limbs.setModel(this.data);
        this.limbEditor.setLimb(null);

        this.model = this.buildModel();
        this.changePose("standing");
    }

    /**
     * Open given modal
     */
    public void openModal(GuiModal modal)
    {
        this.currentModal = modal;
        modal.initiate();
    }

    /**
     * Build the model from data model
     */
    public ModelCustom buildModel()
    {
        try
        {
            File objModel = null;

            if (ClientProxy.actorPack.pack.models.containsKey(this.modelName))
            {
                objModel = ClientProxy.actorPack.pack.models.get(this.modelName).objModel;
            }

            return new ModelParser(objModel).parseModel(this.data, ModelCustom.class);
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
     * Change pose of the model
     */
    private void changePose(String name)
    {
        Model.Pose pose = this.data.poses.get(name);

        if (pose != null)
        {
            this.model.pose = pose;
            this.limbEditor.setPose(pose);

            if (this.pose != null)
            {
                this.pose.displayString = name;
            }
        }
    }

    /**
     * Set texture
     */
    private void setTexture(String texture)
    {
        this.textureRL = new ResourceLocation(texture);
    }

    /**
     * Get currently editing pose
     */
    public Model.Pose getCurrentLimbPose()
    {
        return this.limbEditor.pose;
    }

    /* Initiate GUI and handle input from other widgets */

    @Override
    public void initGui()
    {
        /* Buttons */
        this.edit = new GuiTextureButton(5, this.width - 107, 5, GuiLimbEditor.GUI).setTexPos(48, 32).setActiveTexPos(48, 48);
        this.save = new GuiTextureButton(0, this.width - 79, 5, GuiLimbEditor.GUI).setTexPos(0, 32).setActiveTexPos(0, 48);
        this.clean = new GuiTextureButton(1, this.width - 52, 5, GuiLimbEditor.GUI).setTexPos(16, 32).setActiveTexPos(16, 48);
        this.back = new GuiTextureButton(-100, this.width - 25, 5, GuiLimbEditor.GUI).setTexPos(32, 32).setActiveTexPos(32, 48);

        this.pose = new GuiButton(2, this.width - 110, this.height - 25, 100, 20, "standing");

        this.addLimb = new GuiTextureButton(3, this.width - 25, 30, GuiLimbEditor.GUI).setTexPos(16, 0).setActiveTexPos(16, 16);
        this.removeLimb = new GuiTextureButton(4, this.width - 25 - 16, 30, GuiLimbEditor.GUI).setTexPos(32, 0).setActiveTexPos(32, 16);

        int cx = this.width / 2;
        int by = this.height - 16 - 2;

        this.showTextures = new GuiTextureButton(6, cx - 8 - 24, by, GuiLimbEditor.GUI).setTexPos(0, 0).setActiveTexPos(0, 16);
        this.swipeArms = new GuiTextureButton(7, cx - 8 + 24, by, GuiLimbEditor.GUI).setTexPos(64, 48).setActiveTexPos(64, 64);
        this.swingLegs = new GuiTextureButton(8, cx - 8, by, GuiLimbEditor.GUI).setTexPos(80, 48).setActiveTexPos(80, 64);
        this.renderItems = new GuiTextureButton(9, cx - 8 + 48, by, GuiLimbEditor.GUI).setTexPos(96, 48).setActiveTexPos(96, 64);
        this.renderAABB = new GuiTextureButton(10, cx - 8 - 48, by, GuiLimbEditor.GUI).setTexPos(48, 0).setActiveTexPos(48, 16);

        this.buttonList.add(this.save);
        this.buttonList.add(this.clean);
        this.buttonList.add(this.edit);
        this.buttonList.add(this.back);

        this.buttonList.add(this.pose);

        this.buttonList.add(this.addLimb);
        this.buttonList.add(this.removeLimb);

        this.buttonList.add(this.showTextures);
        this.buttonList.add(this.swipeArms);
        this.buttonList.add(this.swingLegs);
        this.buttonList.add(this.renderItems);
        this.buttonList.add(this.renderAABB);

        /* Custom GUI controls */
        this.limbEditor.initiate(10, 47);
        this.limbs.updateRect(this.width - 111, 47, 102, this.height - 47 - 30);

        this.texturePicker.updateRect(120, -1, this.width - 240, this.height + 2);
        this.texturePicker.setHidden(true);

        /* Initiate the modal */
        if (this.currentModal != null)
        {
            this.currentModal.initiate();
        }
    }

    /**
     * Button handler
     *
     * This monolithic method is responsible for showing modals and executing
     * some actions on the press of button.
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.openModal(new GuiInputModal(SAVE, this, this.fontRenderer).setInput(this.modelName).setLabel(I18n.format("blockbuster.gui.me.save_modal")));
        }
        else if (button.id == 1)
        {
            this.openModal(new GuiNewModal(NEW, this, this.fontRenderer).setLabel(I18n.format("blockbuster.gui.me.new_modal")));
        }
        else if (button.id == 2)
        {
            GuiPoseModal modal = new GuiPoseModal(ADD_POSE, REMOVE_POSE, SELECT_POSE, this, this.fontRenderer);
            String pose = "";

            for (Map.Entry<String, Model.Pose> entry : this.data.poses.entrySet())
            {
                if (entry.getValue().equals(this.model.pose))
                {
                    pose = entry.getKey();
                    break;
                }
            }

            modal.setSelected(pose);
            this.openModal(modal);
        }
        else if (button.id == 3)
        {
            this.openModal(new GuiInputModal(ADD_LIMB, this, this.fontRenderer).setLabel(I18n.format("blockbuster.gui.me.add_limb_modal")));
        }
        else if (button.id == 4 && this.limbs.limb != null)
        {
            this.limbs.removeLimb();
            this.rebuildModel();
        }
        else if (button.id == 5)
        {
            this.openModal(new GuiModelModal(MODEL_PROPS, this, this.fontRenderer).setModel(this.data).setLabel(I18n.format("blockbuster.gui.me.model_props_modal")));
        }
        else if (button.id == 6)
        {
            this.texturePicker.setHidden(false);
        }
        else if (button.id == 7)
        {
            this.swipe = 6;
        }
        else if (button.id == 8)
        {
            this.swinging = !this.swinging;
        }
        else if (button.id == 9)
        {
            this.items = !this.items;
        }
        else if (button.id == 10)
        {
            this.aabb = !this.aabb;
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
            this.limbEditor.changeParent(((GuiParentModal) modal).parents.getSelected());
            this.rebuildModel();
        }
        else if (button.id == ADD_LIMB)
        {
            this.limbs.addLimb(((GuiInputModal) modal).getInput());
            this.rebuildModel();
        }
        else if (button.id == MODEL_PROPS)
        {
            if (!this.editModelProperties((GuiModelModal) modal)) return;
        }
        else if (button.id == SAVE)
        {
            if (!this.saveModel(((GuiInputModal) modal).getInput())) return;
        }
        else if (button.id == NEW)
        {
            if (!this.newModel(((GuiNewModal) this.currentModal).models.selected)) return;
        }
        else if (button.id == ADD_POSE)
        {
            GuiPoseModal poses = (GuiPoseModal) modal;

            this.addPose(poses.getName(), poses.getSelected());
            return;
        }
        else if (button.id == REMOVE_POSE)
        {
            this.removePose(((GuiPoseModal) modal).getSelected());
            return;
        }
        else if (button.id == SELECT_POSE)
        {
            this.changePose(((GuiPoseModal) modal).getSelected());
            return;
        }

        this.currentModal = null;
    }

    /**
     * Edit model properties
     *
     * This method is responsible for extracting data from {@link GuiModelModal}
     * and setting those values to currently editing model.
     */
    private boolean editModelProperties(GuiModelModal modal)
    {
        try
        {
            String name = modal.name.getText();
            float[] scale = new float[] {Float.parseFloat(modal.scale.a.getText()), Float.parseFloat(modal.scale.b.getText()), Float.parseFloat(modal.scale.c.getText())};
            int[] texture = new int[] {Integer.parseInt(modal.textureSize.a.getText()), Integer.parseInt(modal.textureSize.b.getText())};

            if (name.isEmpty() || scale[0] <= 0 || scale[1] <= 0 || scale[2] <= 0 || texture[0] <= 0 || texture[1] <= 0)
            {
                return false;
            }

            String defaultTexture = modal.texture.getText();
            ResourceLocation oldTexture = this.data.defaultTexture;

            this.data.name = name;
            this.data.scale = scale;
            this.data.texture = texture;
            this.data.defaultTexture = defaultTexture.isEmpty() ? null : new ResourceLocation(defaultTexture);
            this.rebuildModel();

            if (oldTexture.equals(this.textureRL) && this.data.defaultTexture != null)
            {
                this.textureRL = this.data.defaultTexture;
            }
        }
        catch (Exception e)
        {}

        return true;
    }

    /**
     * Save model
     *
     * This method is responsible for saving model into users's config folder.
     */
    private boolean saveModel(String name)
    {
        if (name.isEmpty())
        {
            return false;
        }

        File folder = new File(ClientProxy.config, "models/" + name);
        File file = new File(folder, "model.json");
        String output = ModelUtils.toJson(this.data);

        boolean exists = folder.exists();

        folder.mkdirs();

        try
        {
            PrintWriter writer = new PrintWriter(file);

            writer.print(output);
            writer.close();

            String key = name;
            Model model = Blockbuster.proxy.models.models.get(key).model;

            if (model != null)
            {
                ModelUtils.copy(this.data.clone(), model);
            }

            ModelCustom.MODELS.put(key, this.buildModel());
            this.modelName = name;

            if (!exists && this.data.defaultTexture == null)
            {
                this.openModal(new GuiAlertModal(0, this, this.fontRenderer).setSize(220, 120).setLabel(I18n.format("blockbuster.gui.me.warning_skins", name, name)));

                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Setup new model based on model cell
     */
    private boolean newModel(ModelCell cell)
    {
        if (cell == null)
        {
            return false;
        }

        String name = cell.key;
        int index = name.indexOf(".") + 1;

        this.textureRL = cell.texture;
        this.modelName = index == -1 ? name : name.substring(index);
        this.setupModel(cell.model);

        return true;
    }

    /**
     * Remove a custom pose from the model (required poses aren't allowed to be
     * removed)
     */
    private boolean removePose(String name)
    {
        if (Model.REQUIRED_POSES.contains(name))
        {
            return false;
        }

        this.data.poses.remove(name);

        this.rebuildModel();
        this.changePose(this.data.poses.keySet().iterator().next());

        return true;
    }

    /**
     * Add a pose to the model
     */
    private boolean addPose(String name, String selected)
    {
        if (name.isEmpty() || this.data.poses.containsKey(name))
        {
            return false;
        }

        String pose = "standing";

        if (this.data.poses.containsKey(selected))
        {
            pose = selected;
        }

        this.data.poses.put(name, this.data.poses.get(pose).clone());
        this.changePose(name);

        return true;
    }

    /**
     * Pick a limb from limb picker
     */
    @Override
    public void pickLimb(Limb limb)
    {
        this.limbEditor.setLimb(limb);
    }

    /**
     * Pick a texture from texture picker
     */
    @Override
    public void pickTexture(String texture)
    {
        this.setTexture(texture);
        this.texturePicker.setHidden(true);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.limbs.setWorldAndResolution(mc, width, height);
        this.texturePicker.setWorldAndResolution(mc, width, height);
    }

    /* Handling input */

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();

        if (this.currentModal == null)
        {
            this.limbs.handleKeyboardInput();
            this.texturePicker.handleKeyboardInput();
        }
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
        int scroll = -Mouse.getEventDWheel();

        boolean inModal = this.currentModal != null;

        /* Zooming the model */
        if (x > 120 && x < this.width - 120 && this.texturePicker.getHidden() && scroll != 0 && !inModal)
        {
            this.scale += Math.copySign(2.0, scroll);
            this.scale = MathHelper.clamp(this.scale, -100, 500);
        }

        if (scroll != 0 && inModal)
        {
            this.currentModal.wheelScroll(x, y, scroll);
        }

        if (!inModal)
        {
            this.limbs.handleMouseInput();
        }

        this.texturePicker.handleMouseInput();

        super.handleMouseInput();
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
        if (this.currentModal == null)
        {
            this.limbEditor.mouseClicked(mouseX, mouseY, mouseButton);

            super.mouseClicked(mouseX, mouseY, mouseButton);

            if (mouseX > 120 && mouseX < this.width - 120)
            {
                this.dragging = true;
                this.prevX = mouseX;
                this.prevY = mouseY;
            }
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

        if (this.currentModal != null)
        {
            this.currentModal.mouseReleased(mouseX, mouseY, state);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /* Updating and rendering */

    /**
     * Update the screen method
     *
     * This method is responsible for updating the properties that are related
     * to animating the gameplay features.
     */
    @Override
    public void updateScreen()
    {
        this.timer++;

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
     * Draw the screen
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        /* Background and beautiful horizontal fade outs */
        this.drawDefaultBackground();
        GuiUtils.drawHorizontalGradientRect(0, 0, 120, this.height, 0x55000000, 0x00000000, this.zLevel);
        GuiUtils.drawHorizontalGradientRect(this.width - 120, 0, this.width, this.height, 0x00000000, 0x55000000, this.zLevel);

        /* Draw the model */
        try
        {
            float scale = this.height / 3;
            float x = this.width / 2;
            float y = this.height / 2;
            float yaw = (x - mouseX) / this.width * 90;
            float pitch = (y + scale + mouseY) / this.height * 90 - 135;

            this.drawModel(x, y, MathHelper.clamp(scale + this.scale, 20, 1000), yaw, pitch, partialTicks);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /* Labels */
        this.fontRenderer.drawStringWithShadow(this.strME, 10, 10, 0xffffff);
        this.fontRenderer.drawStringWithShadow(this.strLimbs, this.width - 105, 35, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.limbEditor.draw(mouseX, mouseY, partialTicks);
        this.limbs.drawScreen(mouseX, mouseY, partialTicks);
        this.texturePicker.drawScreen(mouseX, mouseY, partialTicks);

        if (this.dragging)
        {
            this.yaw = -(this.prevX - mouseX);
            this.pitch = this.prevY - mouseY;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(GuiLimbEditor.GUI);
        this.drawTexturedModalRect(this.pose.x - 18, this.pose.y + 2, 64, 32, 16, 16);

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
     * Totally stole it from Metamorph's code from {@link GuiUtils}. I hate to
     * copy and paste code like this, but unfortunately, there are too much
     * lines of code that depend on each other and cannot be separated.
     */
    private void drawModel(float x, float y, float scale, float yaw, float pitch, float ticks)
    {
        /* Extending model's rendering range, otherwise it gets clipped */
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, this.width, this.height, 0.0D, 1000.0D, 3000000.0D);
        GlStateManager.matrixMode(5888);

        float factor = 0.0625F;
        float oldSwing = this.model.swingProgress;

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

        float limbSwing = this.swing + ticks;

        this.model.swingProgress = this.swipe == -1 ? 0 : MathHelper.clamp(1.0F - (this.swipe - 1.0F * ticks) / 6.0F, 0.0F, 1.0F);
        this.model.setLivingAnimations(this.dummy, 0, 0, ticks);
        this.model.setRotationAngles(limbSwing, this.swingAmount, this.timer, yaw, pitch, factor, this.dummy);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.translate(0, -this.model.pose.size[1] / 2, 0);

        GlStateManager.pushMatrix();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.scale(this.data.scale[0], this.data.scale[1], this.data.scale[2]);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        this.model.render(this.dummy, 0, 0, this.timer, yaw, pitch, factor);

        if (this.items)
        {
            ItemRenderer.renderItems(this.dummy, this.model, limbSwing, this.swingAmount, ticks, this.timer, yaw, pitch, factor);
        }

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

        this.model.swingProgress = oldSwing;
    }

    /**
     * Render model's hit box
     *
     * Just like in Minecraft's world when you hit F3 + H. This method renders
     * similar box, but in model editor.
     */
    private void renderAABB()
    {
        Model.Pose current = this.getCurrentLimbPose();

        if (current == null)
        {
            return;
        }

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

        RenderGlobal.drawBoundingBox(minX, minY, minZ, maxX, maxY, maxZ, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
}