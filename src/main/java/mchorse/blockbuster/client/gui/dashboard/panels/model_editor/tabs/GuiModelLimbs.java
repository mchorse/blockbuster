package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.api.Model.Limb.Holding;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiListModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiMessageModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiPromptModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElements;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiCirculate;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelLimbs extends GuiModelEditorTab
{
    private GuiButtonElement<GuiTextureButton> addLimb;
    private GuiButtonElement<GuiTextureButton> removeLimb;
    private GuiButtonElement<GuiButton> renameLimb;
    private GuiButtonElement<GuiButton> parentLimb;
    private GuiDelegateElement modal;

    private GuiStringListElement limbList;

    private GuiElements first;
    private GuiElements second;

    private GuiButtonElement<GuiButton> toggle;

    /* First category */
    private GuiThreeElement size;
    private GuiTwoElement texture;
    private GuiThreeElement anchor;
    private GuiThreeElement origin;

    /* Second category */
    private GuiThreeElement color;
    private GuiTrackpadElement opacity;
    private GuiButtonElement<GuiCheckBox> mirror;
    private GuiButtonElement<GuiCheckBox> lighting;
    private GuiButtonElement<GuiCheckBox> shading;
    private GuiButtonElement<GuiCheckBox> is3D;

    private GuiButtonElement<GuiCirculate> holding;
    private GuiButtonElement<GuiCheckBox> swiping;
    private GuiButtonElement<GuiCheckBox> looking;
    private GuiButtonElement<GuiCheckBox> swinging;
    private GuiButtonElement<GuiCheckBox> idle;
    private GuiButtonElement<GuiCheckBox> invert;

    public GuiModelLimbs(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.limbList = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbList.resizer().set(0, 20, 100, 0).parent(this.area).h.set(1, Measure.RELATIVE, -42);
        this.limbList.resizer().x.set(1, Measure.RELATIVE, -100);
        this.children.add(this.limbList);

        /* First category */
        this.first = new GuiElements();
        this.size = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.size[0] = values[0].intValue();
            this.panel.limb.size[1] = values[1].intValue();
            this.panel.limb.size[2] = values[2].intValue();
            this.panel.rebuildModel();
        });
        this.size.setLimit(1, 8192, true);
        this.texture = new GuiTwoElement(mc, (values) ->
        {
            this.panel.limb.texture[0] = values[0].intValue();
            this.panel.limb.texture[1] = values[1].intValue();
            this.panel.rebuildModel();
        });
        this.texture.setLimit(0, 8192, true);
        this.anchor = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.anchor[0] = values[0];
            this.panel.limb.anchor[1] = values[1];
            this.panel.limb.anchor[2] = values[2];
            this.panel.rebuildModel();
        });
        this.origin = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.origin[0] = values[0];
            this.panel.limb.origin[1] = values[1];
            this.panel.limb.origin[2] = values[2];
            this.panel.rebuildModel();
        });

        this.size.resizer().parent(this.area).set(10, 40, 120, 20);
        this.texture.resizer().relative(this.size.resizer()).set(0, 35, 120, 20);
        this.anchor.resizer().relative(this.texture.resizer()).set(0, 35, 120, 20);
        this.origin.resizer().relative(this.anchor.resizer()).set(0, 35, 120, 20);

        this.first.add(this.size, this.texture, this.anchor, this.origin);

        /* Second category */
        this.second = new GuiElements();
        this.color = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.color[0] = values[0];
            this.panel.limb.color[1] = values[1];
            this.panel.limb.color[2] = values[2];
        });
        this.color.setLimit(0, 1);
        this.opacity = new GuiTrackpadElement(mc, "Opacity", (value) -> this.panel.limb.opacity = value);
        this.opacity.setLimit(0, 1);
        this.mirror = GuiButtonElement.checkbox(mc, "Mirror", false, (b) ->
        {
            this.panel.limb.mirror = b.button.isChecked();
            this.panel.rebuildModel();
        });
        this.lighting = GuiButtonElement.checkbox(mc, "Lighting", false, (b) -> this.panel.limb.lighting = b.button.isChecked());
        this.shading = GuiButtonElement.checkbox(mc, "Shading", false, (b) -> this.panel.limb.shading = b.button.isChecked());
        this.is3D = GuiButtonElement.checkbox(mc, "3D", false, (b) -> this.panel.limb.is3D = b.button.isChecked());

        this.holding = new GuiButtonElement<GuiCirculate>(mc, new GuiCirculate(0, 0, 0, 0, 0), (b) -> this.panel.limb.holding = Holding.values()[b.button.getValue()]);
        this.holding.button.addLabel("None");
        this.holding.button.addLabel("Right");
        this.holding.button.addLabel("Left");
        this.swiping = GuiButtonElement.checkbox(mc, "Swiping", false, (b) -> this.panel.limb.swiping = b.button.isChecked());
        this.looking = GuiButtonElement.checkbox(mc, "Looking", false, (b) -> this.panel.limb.looking = b.button.isChecked());
        this.swinging = GuiButtonElement.checkbox(mc, "Swinging", false, (b) -> this.panel.limb.swinging = b.button.isChecked());
        this.idle = GuiButtonElement.checkbox(mc, "Idle", false, (b) -> this.panel.limb.idle = b.button.isChecked());
        this.invert = GuiButtonElement.checkbox(mc, "Invert", false, (b) -> this.panel.limb.invert = b.button.isChecked());

        this.color.resizer().parent(this.area).set(10, 40, 120, 20);
        this.opacity.resizer().relative(this.color.resizer()).set(0, 25, 120, 20);

        this.mirror.resizer().relative(this.opacity.resizer()).set(0, 25, 60, 11);
        this.is3D.resizer().relative(this.mirror.resizer()).set(0, 16, 60, 11);

        this.lighting.resizer().relative(this.opacity.resizer()).set(60, 25, 60, 11);
        this.shading.resizer().relative(this.lighting.resizer()).set(0, 16, 60, 11);

        this.holding.resizer().relative(this.is3D.resizer()).set(0, 25, 56, 20);
        this.swiping.resizer().relative(this.holding.resizer()).set(60, 6, 60, 11);

        this.looking.resizer().relative(this.holding.resizer()).set(0, 25, 60, 11);
        this.idle.resizer().relative(this.looking.resizer()).set(60, 0, 60, 11);
        this.swinging.resizer().relative(this.looking.resizer()).set(0, 16, 60, 11);
        this.invert.resizer().relative(this.swinging.resizer()).set(60, 0, 60, 11);

        this.second.add(this.color, this.opacity, this.mirror, this.lighting, this.shading, this.is3D, this.holding, this.swiping, this.looking, this.swinging, this.idle, this.invert);
        this.second.setVisible(false);
        this.children.add(this.first, this.second);

        /* Buttons */
        this.addLimb = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.addLimb());
        this.removeLimb = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removeLimb());
        this.renameLimb = GuiButtonElement.button(mc, "Rename", (b) -> this.renameLimb());
        this.parentLimb = GuiButtonElement.button(mc, "Parent", (b) -> this.parentLimb());

        this.toggle = GuiButtonElement.button(mc, "<", (b) ->
        {
            this.first.setVisible(!this.first.isVisible());
            this.second.setVisible(!this.first.isVisible());
        });

        this.addLimb.resizer().set(0, 2, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -38);
        this.removeLimb.resizer().set(20, 0, 16, 16).relative(this.addLimb.resizer());

        this.toggle.resizer().set(10, 0, 20, 20).parent(this.area).y.set(1, Measure.RELATIVE, -30);
        this.renameLimb.resizer().set(23, 0, 44, 20).relative(this.toggle.resizer());
        this.parentLimb.resizer().set(47, 0, 50, 20).relative(this.renameLimb.resizer());

        this.children.add(this.addLimb, this.removeLimb, this.renameLimb, this.parentLimb, this.toggle);

        this.modal = new GuiDelegateElement(mc, null);
        this.modal.resizer().set(0, 0, 1, 1, Measure.RELATIVE).parent(this.area);
        this.children.add(this.modal);
    }

    private void addLimb()
    {
        this.modal.setDelegate(new GuiPromptModal(mc, this.modal, "Type a new name for a new limb:", (text) -> this.addLimb(text)).setValue(this.panel.limb.name));
    }

    private void addLimb(String text)
    {
        this.panel.model.addLimb(text);
        this.panel.setLimb(text);
        this.limbList.add(text);
        this.limbList.setCurrent(text);
        this.panel.rebuildModel();
    }

    private void removeLimb()
    {
        int size = this.panel.model.limbs.size();

        if (size == this.panel.model.getLimbCount(this.panel.limb))
        {
            this.modal.setDelegate(new GuiMessageModal(this.mc, this.modal, "You can't remove last limb..."));
        }
        else
        {
            this.panel.model.removeLimb(this.panel.limb);

            String newLimb = this.panel.model.limbs.keySet().iterator().next();

            this.fillData(this.panel.model);
            this.setLimb(newLimb);
            this.panel.rebuildModel();
        }
    }

    private void renameLimb()
    {
        this.modal.setDelegate(new GuiPromptModal(mc, this.modal, "Rename current limb to a new name:", (text) -> this.renameLimb(text)).setValue(this.panel.limb.name));
    }

    private void renameLimb(String text)
    {
        if (this.panel.model.renameLimb(this.panel.limb, text))
        {
            this.limbList.replace(text);
            this.panel.rebuildModel();
        }
    }

    private void parentLimb()
    {
        this.modal.setDelegate(new GuiListModal(mc, this.modal, "Choose the parent limb for currently selected limb...", (text) -> this.parentLimb(text)).addValues(this.panel.model.limbs.keySet()).setValue(this.panel.limb.parent));
    }

    private void parentLimb(String text)
    {
        this.panel.limb.parent = text;
        this.panel.rebuildModel();
    }

    private void setLimb(String str)
    {
        this.panel.setLimb(str);
        this.fillLimbData(this.panel.limb);
    }

    public void setCurrent(String str)
    {
        this.limbList.setCurrent(str);
        this.fillLimbData(this.panel.limb);
    }

    public void fillData(Model model)
    {
        this.limbList.clear();
        this.limbList.add(model.limbs.keySet());
        this.limbList.sort();
    }

    public void fillLimbData(Limb limb)
    {
        this.size.setValues(limb.size[0], limb.size[1], limb.size[2]);
        this.texture.setValues(limb.texture[0], limb.texture[1]);
        this.anchor.setValues(limb.anchor[0], limb.anchor[1], limb.anchor[2]);
        this.origin.setValues(limb.origin[0], limb.origin[1], limb.origin[2]);
        this.color.setValues(limb.color[0], limb.color[1], limb.color[2]);
        this.opacity.trackpad.setValue(limb.opacity);
        this.mirror.button.setIsChecked(limb.mirror);
        this.lighting.button.setIsChecked(limb.lighting);
        this.shading.button.setIsChecked(limb.shading);
        this.is3D.button.setIsChecked(limb.is3D);

        this.holding.button.setValue(limb.holding.ordinal());
        this.swiping.button.setIsChecked(limb.swiping);
        this.looking.button.setIsChecked(limb.looking);
        this.swinging.button.setIsChecked(limb.swinging);
        this.idle.button.setIsChecked(limb.idle);
        this.invert.button.setIsChecked(limb.invert);
    }
}