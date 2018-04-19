package mchorse.blockbuster.model_editor.elements;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiCirculate;
import mchorse.blockbuster.model_editor.GuiModelEditor;
import mchorse.blockbuster.model_editor.elements.GuiThreeInput.IMultiInputListener;
import mchorse.blockbuster.model_editor.elements.modals.GuiInputModal;
import mchorse.blockbuster.model_editor.elements.modals.GuiParentModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.client.config.GuiCheckBox;

/**
 * Limb editor GUI view
 *
 * This thing is going to be responsible for editing current selected limb,
 * its data and also current pose's transformations.
 */
public class GuiLimbEditor implements IMultiInputListener, GuiResponder
{
    public static final ResourceLocation GUI = new ResourceLocation("blockbuster:textures/gui/model_editor.png");

    /* Field IDs */

    /* Meta */
    private static final int NAME = 0;
    private static final int PARENT = 1;

    /* Visual properties */
    private static final int MIRROR = 2;
    private static final int TEXTURE = 3;
    private static final int SIZE = 4;
    private static final int ANCHOR = 5;
    private static final int COLOR = 6;
    private static final int OPACITY = 7;

    /* Gameplay properties */
    private static final int LOOKING = 8;
    private static final int IDLE = 9;
    private static final int SWINGING = 10;
    private static final int SWIPING = 11;
    private static final int INVERT = 12;
    private static final int HOLDING = 13;

    /* Pose properties */
    private static final int TRANSLATE = 14;
    private static final int SCALE = 15;
    private static final int ROTATE = 16;
    private static final int ORIGIN = 17;

    /* Strings */
    private final String strNoLimbs = I18n.format("blockbuster.gui.me.no_limbs");
    private final String strVisual = I18n.format("blockbuster.gui.me.visual");
    private final String strGameplay = I18n.format("blockbuster.gui.me.gameplay");
    private final String strPose = I18n.format("blockbuster.gui.me.pose");

    /* Data */

    /**
     * Currently editing limb
     */
    public Model.Limb limb;

    /**
     * Current pose
     */
    public Model.Pose pose;

    /* GUI fields */

    /**
     * Parent screen
     */
    private GuiModelEditor editor;

    /**
     * List of buttons to be handled
     */
    private List<GuiButton> buttons = new ArrayList<GuiButton>();

    /* Buttons for changing stuff */
    private GuiButton name;
    private GuiButton parent;

    /* Visual properties */
    private GuiCheckBox mirror;
    private GuiTwoInput texture;
    private GuiThreeInput size;
    private GuiThreeInput anchor;
    private GuiThreeInput color;
    private GuiTextField opacity;

    /* Gameplay features */
    private GuiCheckBox looking;
    private GuiCheckBox idle;
    private GuiCheckBox swinging;
    private GuiCheckBox swiping;
    private GuiCheckBox invert;
    private GuiCirculate holding;

    /* Poses */
    private GuiThreeInput translate;
    private GuiThreeInput scale;
    private GuiThreeInput rotate;
    private GuiThreeInput origin;

    /* Stuff */
    private int category;

    private GuiButton next;
    private GuiButton prev;

    /**
     * Initiate all GUI fields here
     *
     * I don't understand why Minecraft's GUI create new fields every time in
     * iniGui (only makes sense for {@link InitGuiEvent}, but then you still can
     * create buttons only once, update their positions and add them to
     * buttonList).
     */
    public GuiLimbEditor(GuiModelEditor editor)
    {
        this.editor = editor;

        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        final int width = 100;

        /* Buttons */
        this.name = new GuiButton(NAME, 0, 0, width, 20, I18n.format("blockbuster.gui.me.name"));
        this.parent = new GuiButton(PARENT, 0, 0, width, 20, I18n.format("blockbuster.gui.me.parent"));

        /* Initiate inputs */
        this.mirror = new GuiCheckBox(MIRROR, 0, 0, I18n.format("blockbuster.gui.me.mirror"), false);
        this.texture = new GuiTwoInput(TEXTURE, font, 0, 0, 0, this);
        this.size = new GuiThreeInput(SIZE, font, 0, 0, 0, this);
        this.anchor = new GuiThreeInput(ANCHOR, font, 0, 0, 0, this);
        this.color = new GuiThreeInput(COLOR, font, 0, 0, 0, this);
        this.opacity = new GuiTextField(OPACITY, font, 0, 0, 0, 16);
        this.opacity.setGuiResponder(this);

        /* Gameplay */
        this.looking = new GuiCheckBox(LOOKING, 0, 0, I18n.format("blockbuster.gui.me.looking"), false);
        this.idle = new GuiCheckBox(IDLE, 0, 0, I18n.format("blockbuster.gui.me.idle"), false);
        this.swinging = new GuiCheckBox(SWINGING, 0, 0, I18n.format("blockbuster.gui.me.swinging"), false);
        this.swiping = new GuiCheckBox(SWIPING, 0, 0, I18n.format("blockbuster.gui.me.swiping"), false);
        this.invert = new GuiCheckBox(INVERT, 0, 0, I18n.format("blockbuster.gui.me.invert"), false);
        this.holding = new GuiCirculate(HOLDING, 0, 0, width, 20);

        this.holding.addLabel(I18n.format("blockbuster.gui.me.no_hands"));
        this.holding.addLabel(I18n.format("blockbuster.gui.me.right_hand"));
        this.holding.addLabel(I18n.format("blockbuster.gui.me.left_hand"));

        /* Poses */
        this.translate = new GuiThreeInput(TRANSLATE, font, 0, 0, width, this);
        this.scale = new GuiThreeInput(SCALE, font, 0, 0, width, this);
        this.rotate = new GuiThreeInput(ROTATE, font, 0, 0, width, this);
        this.origin = new GuiThreeInput(ORIGIN, font, 0, 0, width, this);

        /* Category buttons */
        this.next = new GuiButton(-1, 0, 0, ">");
        this.prev = new GuiButton(-2, 0, 0, "<");
    }

    private boolean isCategory(int index)
    {
        return this.category == index || this.category == -1;
    }

    /**
     * Change limb's name
     */
    public void changeName(String newName)
    {
        if (newName.isEmpty())
        {
            return;
        }

        /* Rename limb name in poses */
        for (Model.Pose pose : this.editor.data.poses.values())
        {
            Model.Transform transform = pose.limbs.remove(this.limb.name);

            pose.limbs.put(newName, transform);
        }

        /* Rename all children limbs */
        for (Model.Limb limb : this.editor.data.limbs.values())
        {
            if (limb.parent.equals(this.limb.name))
            {
                limb.parent = newName;
            }
        }

        /* And finally remap the limb name to the new name */
        this.editor.data.limbs.remove(this.limb.name);
        this.editor.data.limbs.put(newName, this.limb);
        this.limb.name = newName;
    }

    /**
     * Change the parent
     */
    public void changeParent(String newParent)
    {
        if (!newParent.isEmpty() && !this.editor.data.limbs.containsKey(newParent))
        {
            return;
        }

        this.limb.parent = newParent;
    }

    /**
     * Set currently editing limb
     */
    public void setLimb(Model.Limb limb)
    {
        this.limb = limb;

        if (limb == null)
        {
            return;
        }

        /* Visual */
        this.mirror.setIsChecked(limb.mirror);
        this.texture.a.setText(String.valueOf(limb.texture[0]));
        this.texture.b.setText(String.valueOf(limb.texture[1]));
        this.size.a.setText(String.valueOf(limb.size[0]));
        this.size.b.setText(String.valueOf(limb.size[1]));
        this.size.c.setText(String.valueOf(limb.size[2]));
        this.anchor.a.setText(String.valueOf(limb.anchor[0]));
        this.anchor.b.setText(String.valueOf(limb.anchor[1]));
        this.anchor.c.setText(String.valueOf(limb.anchor[2]));
        this.color.a.setText(String.valueOf(limb.color[0]));
        this.color.b.setText(String.valueOf(limb.color[1]));
        this.color.c.setText(String.valueOf(limb.color[2]));
        this.opacity.setText(String.valueOf(limb.opacity));

        /* Gameplay */
        this.looking.setIsChecked(limb.looking);
        this.idle.setIsChecked(limb.idle);
        this.swinging.setIsChecked(limb.swinging);
        this.swiping.setIsChecked(limb.swiping);
        this.invert.setIsChecked(limb.invert);
        this.holding.setValue(limb.holding.isEmpty() ? 0 : (limb.holding.equals("right") ? 1 : 2));

        /* OBJ origin */
        this.origin.a.setText(String.valueOf(limb.origin[0]));
        this.origin.b.setText(String.valueOf(limb.origin[1]));
        this.origin.c.setText(String.valueOf(limb.origin[2]));

        this.updatePoseFields();
    }

    /**
     * Set currently used pose
     */
    public void setPose(Model.Pose pose)
    {
        this.pose = pose;

        this.updatePoseFields();
    }

    /**
     * Sets the pose variables based on current limb's transform in the current
     * pose.
     */
    private void updatePoseFields()
    {
        if (this.limb == null)
        {
            return;
        }

        Model.Transform transform = this.pose.limbs.get(this.limb.name);

        if (transform != null)
        {
            this.translate.a.setText(String.valueOf(transform.translate[0]));
            this.translate.b.setText(String.valueOf(transform.translate[1]));
            this.translate.c.setText(String.valueOf(transform.translate[2]));

            this.scale.a.setText(String.valueOf(transform.scale[0]));
            this.scale.b.setText(String.valueOf(transform.scale[1]));
            this.scale.c.setText(String.valueOf(transform.scale[2]));

            this.rotate.a.setText(String.valueOf(transform.rotate[0]));
            this.rotate.b.setText(String.valueOf(transform.rotate[1]));
            this.rotate.c.setText(String.valueOf(transform.rotate[2]));
        }
    }

    /**
     * Initiate here all GUI stuff
     *
     * This method is responsible for initiating categories of different
     * widgets which is in current category.
     */
    public void initiate(int x, int y)
    {
        int width = 100;
        boolean full = this.editor.height > 360;

        this.category = full ? -1 : (this.category == -1 ? 0 : this.category);

        this.name.xPosition = x;
        this.name.yPosition = y;
        y += 25;
        this.parent.xPosition = x;
        this.parent.yPosition = y;
        y += 28;

        this.buttons.clear();
        this.buttons.add(this.name);
        this.buttons.add(this.parent);

        if (this.category == 0 || full)
        {
            this.mirror.xPosition = x;
            this.mirror.yPosition = y;
            y += 15;
            this.texture.update(x, y, width);
            y += 20;
            this.size.update(x, y, width);
            y += 20;
            this.anchor.update(x, y, width);
            y += 20;
            this.color.update(x, y, width);
            y += 20;
            this.opacity.xPosition = x + 1;
            this.opacity.yPosition = y + 1;
            this.opacity.width = width - 2;
            y += 23;

            this.buttons.add(this.mirror);
        }

        if (this.category == 1 || full)
        {
            this.looking.xPosition = x;
            this.looking.yPosition = y;
            y += 16;
            this.idle.xPosition = x;
            this.idle.yPosition = y;
            y += 16;
            this.swinging.xPosition = x;
            this.swinging.yPosition = y;
            y += 16;
            this.swiping.xPosition = x;
            this.swiping.yPosition = y;
            y += 16;
            this.invert.xPosition = x;
            this.invert.yPosition = y;
            y += 20;
            this.holding.xPosition = x;
            this.holding.yPosition = y;
            y += 25;

            this.buttons.add(this.looking);
            this.buttons.add(this.idle);
            this.buttons.add(this.swinging);
            this.buttons.add(this.swiping);
            this.buttons.add(this.invert);
            this.buttons.add(this.holding);
        }

        if (this.category == 2 || full)
        {
            this.translate.update(x, y, width);
            y += 20;
            this.scale.update(x, y, width);
            y += 20;
            this.rotate.update(x, y, width);
            y += 25;
            this.origin.update(x, y, width);
            y += 25;
        }

        if (!full)
        {
            int w = 20;

            this.prev.xPosition = x;
            this.next.xPosition = x + width - w;
            this.next.yPosition = this.prev.yPosition = y;
            this.next.width = this.prev.width = w;

            this.buttons.add(this.next);
            this.buttons.add(this.prev);
        }
    }

    /**
     * Click only on stuff that are in current category
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.limb == null)
        {
            return;
        }

        if (this.isCategory(0))
        {
            this.texture.mouseClicked(mouseX, mouseY, mouseButton);
            this.size.mouseClicked(mouseX, mouseY, mouseButton);
            this.anchor.mouseClicked(mouseX, mouseY, mouseButton);
            this.color.mouseClicked(mouseX, mouseY, mouseButton);
            this.opacity.mouseClicked(mouseX, mouseY, mouseButton);
        }

        this.checkButtons(mouseX, mouseY, mouseButton);

        if (this.pose == null)
        {
            return;
        }

        if (this.isCategory(2))
        {
            this.translate.mouseClicked(mouseX, mouseY, mouseButton);
            this.scale.mouseClicked(mouseX, mouseY, mouseButton);
            this.rotate.mouseClicked(mouseX, mouseY, mouseButton);
            this.origin.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Check whether buttons were clicked
     *
     * This method is a pretty much copy paste from {@link GuiScreen}'s method
     * mouseClicked.
     */
    private void checkButtons(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            for (int i = 0; i < this.buttons.size(); ++i)
            {
                GuiButton button = this.buttons.get(i);

                if (button.mousePressed(this.editor.mc, mouseX, mouseY))
                {
                    button.playPressSound(this.editor.mc.getSoundHandler());
                    this.actionPerformed(button);

                    break;
                }
            }
        }
    }

    /**
     * Key typed
     *
     * Delegates key typing only for those fields that are in the current
     * category.
     */
    public void keyTyped(char typedChar, int keyCode)
    {
        if (this.limb == null)
        {
            return;
        }

        if (this.isCategory(0))
        {
            this.texture.keyTyped(typedChar, keyCode);
            this.size.keyTyped(typedChar, keyCode);
            this.anchor.keyTyped(typedChar, keyCode);
            this.color.keyTyped(typedChar, keyCode);
            this.opacity.textboxKeyTyped(typedChar, keyCode);
        }

        if (this.pose == null)
        {
            return;
        }

        if (this.isCategory(2))
        {
            this.translate.keyTyped(typedChar, keyCode);
            this.scale.keyTyped(typedChar, keyCode);
            this.rotate.keyTyped(typedChar, keyCode);
            this.origin.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Draw limb editor
     *
     * This method is responsible for drawing all buttons and widgets on the
     * screen. The complex thing in this method is rendering of "categories".
     * See how much ifs we've got here.
     */
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        FontRenderer font = this.editor.mc.fontRendererObj;

        if (this.limb == null)
        {
            font.drawStringWithShadow(this.strNoLimbs, this.name.xPosition, this.name.yPosition - 15, 0xffffff);
            return;
        }

        font.drawStringWithShadow(this.limb.name, this.name.xPosition, this.name.yPosition - 15, 0xffffff);

        if (this.category != -1)
        {
            String cat = this.category == 0 ? this.strVisual : (this.category == 1 ? this.strGameplay : this.strPose);

            this.editor.drawCenteredString(font, cat, this.prev.xPosition + 49, this.next.yPosition + 6, 0xffffffff);
        }

        if (this.isCategory(0))
        {
            this.texture.draw();
            this.size.draw();
            this.anchor.draw();
            this.color.draw();
            this.opacity.drawTextBox();

            /* Icons for visual category */
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.editor.mc.renderEngine.bindTexture(GUI);
            this.editor.drawTexturedModalRect(this.texture.a.xPosition + 100, this.texture.a.yPosition, 96, 0, 16, 16);
            this.editor.drawTexturedModalRect(this.size.a.xPosition + 100, this.size.a.yPosition, 96, 16, 16, 16);
            this.editor.drawTexturedModalRect(this.anchor.a.xPosition + 100, this.anchor.a.yPosition, 64, 16, 16, 16);
            this.editor.drawTexturedModalRect(this.color.a.xPosition + 100, this.color.a.yPosition, 80, 32, 16, 16);
            this.editor.drawTexturedModalRect(this.opacity.xPosition + 100, this.opacity.yPosition, 96, 32, 16, 16);
        }

        for (GuiButton button : this.buttons)
        {
            button.drawButton(this.editor.mc, mouseX, mouseY);
        }

        if (this.pose == null)
        {
            return;
        }

        if (this.isCategory(2))
        {
            this.translate.draw();
            this.rotate.draw();
            this.scale.draw();
            this.origin.draw();

            /* Icons for pose */
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.editor.mc.renderEngine.bindTexture(GUI);
            this.editor.drawTexturedModalRect(this.translate.a.xPosition + 100, this.translate.a.yPosition, 64, 0, 16, 16);
            this.editor.drawTexturedModalRect(this.scale.a.xPosition + 100, this.scale.a.yPosition, 80, 0, 16, 16);
            this.editor.drawTexturedModalRect(this.rotate.a.xPosition + 100, this.rotate.a.yPosition, 80, 16, 16, 16);
            this.editor.drawTexturedModalRect(this.origin.a.xPosition + 100, this.origin.a.yPosition, 112, 32, 16, 16);
        }
    }

    /* Methods for changing values */

    /**
     * Action performed
     *
     * This method is responsible for changing values based on the button
     * clicks. Works with {@link GuiCheckBox} and {@link GuiCirculate}.
     *
     * This method is also responsible for triggering name and parent changing
     * modals.
     */
    private void actionPerformed(GuiButton button)
    {
        if (this.limb == null)
        {
            return;
        }

        if (button.id == NAME)
        {
            this.editor.openModal(new GuiInputModal(GuiModelEditor.CHANGE_NAME, this.editor, this.editor.mc.fontRendererObj).setInput(this.limb.name).setLabel(I18n.format("blockbuster.gui.me.limb_name_modal")));
        }
        if (button.id == PARENT)
        {
            this.editor.openModal(new GuiParentModal(GuiModelEditor.CHANGE_PARENT, this.limb, this.editor.data, this.editor, this.editor.mc.fontRendererObj).setLabel(I18n.format("blockbuster.gui.me.limb_parent_modal")));
        }

        if (button.id == -1 || button.id == -2)
        {
            boolean next = button.id == -1;

            this.category += next ? 1 : -1;
            this.category = this.category > 2 ? 0 : (this.category < 0 ? 2 : this.category);
            this.initiate(this.name.xPosition, this.name.yPosition);
        }

        if (this.isCategory(0))
        {
            if (button.id == MIRROR)
            {
                this.limb.mirror = this.mirror.isChecked();
                this.editor.rebuildModel();
            }
        }

        if (this.isCategory(1))
        {
            if (button.id == LOOKING)
            {
                this.limb.looking = this.looking.isChecked();
            }
            if (button.id == IDLE)
            {
                this.limb.idle = this.idle.isChecked();
            }
            if (button.id == SWINGING)
            {
                this.limb.swinging = this.swinging.isChecked();
            }
            if (button.id == SWIPING)
            {
                this.limb.swiping = this.swiping.isChecked();
            }
            if (button.id == INVERT)
            {
                this.limb.invert = this.invert.isChecked();
            }
            if (button.id == HOLDING)
            {
                this.holding.toggle();

                int value = this.holding.getValue();
                this.limb.holding = value == 0 ? "" : (value == 1 ? "right" : "left");
                this.editor.rebuildModel();
            }
        }
    }

    /**
     * Callback method from {@link IMultiInputListener}.
     *
     * This callback will apply values from multiple slotted inputs to the poses
     * and some of visual properties (with model rebuilding).
     */
    @Override
    public void setValue(int id, int subset, String value)
    {
        if (this.limb == null)
        {
            return;
        }

        try
        {
            float val = Float.parseFloat(value);

            if (id == TEXTURE && val >= 0)
            {
                this.limb.texture[subset] = (int) val;
                this.editor.rebuildModel();
            }
            if (id == ANCHOR)
            {
                this.limb.anchor[subset] = val;
                this.editor.rebuildModel();
            }
            if (id == SIZE && val > 0)
            {
                this.limb.size[subset] = (int) val;
                this.editor.rebuildModel();
            }
            if (id == COLOR && val >= 0 && val <= 1)
            {
                this.limb.color[subset] = val;
            }
            if (id == ORIGIN)
            {
                this.limb.origin[subset] = val;
                this.editor.rebuildModel();
            }

            if (this.pose == null)
            {
                return;
            }

            Model.Transform trans = this.pose.limbs.get(this.limb.name);

            if (id == TRANSLATE)
            {
                trans.translate[subset] = val;
            }
            if (id == SCALE)
            {
                trans.scale[subset] = val;
            }
            if (id == ROTATE)
            {
                trans.rotate[subset] = val;
            }
        }
        catch (NumberFormatException e)
        {}
    }

    @Override
    public void setEntryValue(int id, boolean value)
    {}

    @Override
    public void setEntryValue(int id, float value)
    {}

    @Override
    public void setEntryValue(int id, String value)
    {
        try
        {
            float val = Float.parseFloat(value);

            if (id == OPACITY)
            {
                this.limb.opacity = val;
            }
        }
        catch (NumberFormatException e)
        {}
    }
}