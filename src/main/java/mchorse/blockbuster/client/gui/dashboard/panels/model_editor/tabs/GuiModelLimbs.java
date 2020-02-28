package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.api.ModelLimb.Holding;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiListModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.blockbuster.client.gui.utils.GuiScrollElement;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.Resizer.Measure;
import mchorse.mclib.client.gui.widgets.buttons.GuiCirculate;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelLimbs extends GuiModelEditorTab
{
    private GuiButtonElement<GuiTextureButton> addLimb;
    private GuiButtonElement<GuiTextureButton> dupeLimb;
    private GuiButtonElement<GuiTextureButton> removeLimb;
    private GuiButtonElement<GuiButton> renameLimb;
    private GuiButtonElement<GuiButton> parentLimb;
    private GuiDelegateElement<IGuiElement> modal;

    private GuiStringListElement limbList;
    private GuiScrollElement scroll;

    /* First category */
    private GuiThreeElement size;
    private GuiTrackpadElement sizeOffset;
    private GuiTwoElement texture;
    private GuiThreeElement anchor;
    private GuiThreeElement origin;

    /* Second category */
    private GuiThreeElement color;
    private GuiTrackpadElement opacity;
    private GuiButtonElement<GuiCheckBox> mirror;
    private GuiButtonElement<GuiCheckBox> lighting;
    private GuiButtonElement<GuiCheckBox> shading;
    private GuiButtonElement<GuiCheckBox> smooth;
    private GuiButtonElement<GuiCheckBox> is3D;

    private GuiButtonElement<GuiCirculate> holding;
    private GuiButtonElement<GuiCirculate> slot;
    private GuiButtonElement<GuiCheckBox> hold;
    private GuiButtonElement<GuiCheckBox> swiping;
    private GuiButtonElement<GuiCheckBox> looking;
    private GuiButtonElement<GuiCheckBox> swinging;
    private GuiButtonElement<GuiCheckBox> idle;
    private GuiButtonElement<GuiCheckBox> invert;
    private GuiButtonElement<GuiCheckBox> wheel;
    private GuiButtonElement<GuiCheckBox> wing;

    public GuiModelLimbs(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        final GuiModelLimbs limbs = this;

        this.title = I18n.format("blockbuster.gui.me.limbs.title");

        this.scroll = new GuiScrollElement(mc)
        {
            @Override
            protected void preDraw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
            {
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.limbs.size"), limbs.size.area.x, limbs.size.area.y - 10, 0xeeeeee);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.limbs.size_offset"), limbs.sizeOffset.area.x, limbs.sizeOffset.area.y - 10, 0xeeeeee);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.limbs.texture"), limbs.texture.area.x, limbs.texture.area.y - 10, 0xeeeeee);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.limbs.anchor"), limbs.anchor.area.x, limbs.anchor.area.y - 10, 0xeeeeee);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.limbs.origin"), limbs.origin.area.x, limbs.origin.area.y - 10, 0xeeeeee);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.limbs.color"), limbs.color.area.x, limbs.color.area.y - 10, 0xeeeeee);
            }
        };
        this.scroll.scroll.scrollSpeed = 15;
        this.scroll.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -50);

        this.limbList = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbList.resizer().set(0, 20, 100, 0).parent(this.area).h(1, -20).x(1, -100);
        this.children.add(this.limbList);

        /* First category */
        this.size = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.size[0] = values[0].intValue();
            this.panel.limb.size[1] = values[1].intValue();
            this.panel.limb.size[2] = values[2].intValue();
            this.panel.rebuildModel();
        });
        this.size.setLimit(1, 8192, true);
        this.sizeOffset = new GuiTrackpadElement(mc, "", (value) ->
        {
            this.panel.limb.sizeOffset = value;
            this.panel.rebuildModel();
        });
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
        this.slot = new GuiButtonElement<GuiCirculate>(mc, new GuiCirculate(0, 0, 0, 0, 0), (b) -> this.panel.limb.slot = ArmorSlot.values()[b.button.getValue()]);
        this.slot.tooltip(I18n.format("blockbuster.gui.me.limbs.slot"), TooltipDirection.LEFT);
        this.hold = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.holding"), false, (b) -> this.panel.limb.hold = b.button.isChecked());

        for (ArmorSlot slot : ArmorSlot.values())
        {
            this.slot.button.addLabel(I18n.format("blockbuster.gui.me.limbs.slots." + slot.name));
        }

        this.size.resizer().parent(this.scroll.area).set(10, 20, 120, 20);
        this.sizeOffset.resizer().relative(this.size.resizer()).set(0, 35, 120, 20);
        this.texture.resizer().relative(this.sizeOffset.resizer()).set(0, 35, 120, 20);
        this.anchor.resizer().relative(this.texture.resizer()).set(0, 35, 120, 20);
        this.origin.resizer().relative(this.anchor.resizer()).set(0, 35, 120, 20);
        this.slot.resizer().relative(this.origin.resizer()).set(0, 22, 120, 20);
        this.hold.resizer().relative(this.slot.resizer()).set(0, 25, 60, 11);

        /* Second category */
        this.color = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.color[0] = values[0];
            this.panel.limb.color[1] = values[1];
            this.panel.limb.color[2] = values[2];
        });
        this.color.setLimit(0, 1);
        this.opacity = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.me.limbs.opacity"), (value) -> this.panel.limb.opacity = value);
        this.opacity.setLimit(0, 1);
        this.mirror = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.mirror"), false, (b) ->
        {
            this.panel.limb.mirror = b.button.isChecked();
            this.panel.rebuildModel();
        });
        this.lighting = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.lighting"), false, (b) -> this.panel.limb.lighting = b.button.isChecked());
        this.shading = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.shading"), false, (b) -> this.panel.limb.shading = b.button.isChecked());
        this.smooth = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.smooth"), false, (b) -> this.panel.limb.smooth = b.button.isChecked());
        this.is3D = GuiButtonElement.checkbox(mc, "3D", false, (b) -> this.panel.limb.is3D = b.button.isChecked());

        this.holding = new GuiButtonElement<GuiCirculate>(mc, new GuiCirculate(0, 0, 0, 0, 0), (b) ->
        {
            this.panel.limb.holding = Holding.values()[b.button.getValue()];
            this.panel.rebuildModel();
        });
        this.holding.tooltip(I18n.format("blockbuster.gui.me.limbs.hold"), TooltipDirection.LEFT);
        this.holding.button.addLabel(I18n.format("blockbuster.gui.me.limbs.none"));
        this.holding.button.addLabel(I18n.format("blockbuster.gui.me.limbs.right"));
        this.holding.button.addLabel(I18n.format("blockbuster.gui.me.limbs.left"));

        this.swiping = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.swiping"), false, (b) -> this.panel.limb.swiping = b.button.isChecked());
        this.looking = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.looking"), false, (b) -> this.panel.limb.looking = b.button.isChecked());
        this.swinging = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.swinging"), false, (b) -> this.panel.limb.swinging = b.button.isChecked());
        this.idle = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.idle"), false, (b) -> this.panel.limb.idle = b.button.isChecked());
        this.invert = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.invert"), false, (b) -> this.panel.limb.invert = b.button.isChecked());
        this.wheel = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.wheel"), false, (b) -> this.panel.limb.wheel = b.button.isChecked());
        this.wing = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.wing"), false, (b) -> this.panel.limb.wing = b.button.isChecked());

        this.color.resizer().relative(this.hold.resizer()).set(0, 40, 120, 20);
        this.opacity.resizer().relative(this.color.resizer()).set(0, 25, 120, 20);

        this.mirror.resizer().relative(this.opacity.resizer()).set(0, 25, 60, 11);
        this.is3D.resizer().relative(this.mirror.resizer()).set(0, 16, 60, 11);

        this.lighting.resizer().relative(this.opacity.resizer()).set(60, 25, 60, 11);
        this.shading.resizer().relative(this.lighting.resizer()).set(0, 16, 60, 11);
        this.smooth.resizer().relative(this.shading.resizer()).set(0, 16, 60, 11);

        this.holding.resizer().relative(this.is3D.resizer()).set(0, 25, 56, 20);

        this.swiping.resizer().relative(this.idle.resizer()).set(0, -16, 60, 11);
        this.looking.resizer().relative(this.holding.resizer()).set(0, 25, 60, 11);
        this.idle.resizer().relative(this.looking.resizer()).set(60, 0, 60, 11);
        this.swinging.resizer().relative(this.looking.resizer()).set(0, 16, 60, 11);
        this.invert.resizer().relative(this.swinging.resizer()).set(60, 0, 60, 11);
        this.wheel.resizer().relative(this.swinging.resizer()).set(0, 16, 60, 11);
        this.wing.resizer().relative(this.wheel.resizer()).set(60, 0, 60, 11);

        this.children.add(this.scroll);
        this.scroll.children.add(this.size, this.sizeOffset, this.texture, this.anchor, this.origin, this.slot, this.hold);
        this.scroll.children.add(this.color, this.opacity, this.mirror, this.lighting, this.shading, this.smooth, this.is3D, this.holding, this.swiping, this.looking, this.swinging, this.idle, this.invert, this.wheel, this.wing);

        /* Buttons */
        this.addLimb = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 32, 32, 32, 48, (b) -> this.addLimb());
        this.dupeLimb = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 48, 32, 48, 48, (b) -> this.dupeLimb());
        this.removeLimb = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 64, 32, 64, 48, (b) -> this.removeLimb());
        this.renameLimb = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.me.limbs.rename"), (b) -> this.renameLimb());
        this.parentLimb = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.me.limbs.parent"), (b) -> this.parentLimb());

        this.addLimb.resizer().set(0, 2, 16, 16).parent(this.area).x(1, -58);
        this.dupeLimb.resizer().set(20, 0, 16, 16).relative(this.addLimb.resizer());
        this.removeLimb.resizer().set(20, 0, 16, 16).relative(this.dupeLimb.resizer());

        this.renameLimb.resizer().set(10, 0, 0, 20).parent(this.area).w(0.5F, -13).y(1, -30);
        this.parentLimb.resizer().set(0, 0, 0, 20).parent(this.area).x(0.5F, 3).w(0.5F, -13).y(1, -30);

        this.children.add(this.addLimb, this.dupeLimb, this.removeLimb, this.renameLimb, this.parentLimb);

        this.modal = new GuiDelegateElement<IGuiElement>(mc, null);
        this.modal.resizer().set(0, 0, 1, 1, Measure.RELATIVE).parent(this.area);
        this.children.add(this.modal);

        this.resize(0, 0);
    }

    private void addLimb()
    {
        this.modal.setDelegate(new GuiPromptModal(mc, this.modal, I18n.format("blockbuster.gui.me.limbs.new_limb"), (text) -> this.addLimb(text)).setValue(this.panel.limb.name));
    }

    private void addLimb(String text)
    {
        if (!this.panel.model.limbs.containsKey(text))
        {
            this.panel.model.addLimb(text);
            this.panel.setLimb(text);
            this.limbList.add(text);
            this.limbList.setCurrent(text);
            this.panel.rebuildModel();
        }
    }

    private void dupeLimb()
    {
        ModelLimb limb = this.panel.limb.clone();

        /* It must be unique name */
        while (this.panel.model.limbs.containsKey(limb.name))
        {
            limb.name += "_copy";
        }

        this.panel.model.addLimb(limb);
        this.panel.setLimb(limb.name);
        this.limbList.add(limb.name);
        this.limbList.setCurrent(limb.name);
        this.panel.rebuildModel();
    }

    private void removeLimb()
    {
        int size = this.panel.model.limbs.size();

        if (size == this.panel.model.getLimbCount(this.panel.limb))
        {
            this.modal.setDelegate(new GuiMessageModal(this.mc, this.modal, I18n.format("blockbuster.gui.me.limbs.last_limb")));
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
        this.modal.setDelegate(new GuiPromptModal(mc, this.modal, I18n.format("blockbuster.gui.me.limbs.rename_limb"), (text) -> this.renameLimb(text)).setValue(this.panel.limb.name));
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
        this.modal.setDelegate(new GuiListModal(mc, this.modal, I18n.format("blockbuster.gui.me.limbs.parent_limb"), (text) -> this.parentLimb(text)).addValues(this.panel.model.limbs.keySet()).setValue(this.panel.limb.parent));
    }

    private void parentLimb(String text)
    {
        if (!this.panel.limb.name.equals(text))
        {
            this.panel.limb.parent = text;
            this.panel.rebuildModel();
        }
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

    public void fillLimbData(ModelLimb limb)
    {
        this.size.setValues(limb.size[0], limb.size[1], limb.size[2]);
        this.sizeOffset.setValue(limb.sizeOffset);
        this.texture.setValues(limb.texture[0], limb.texture[1]);
        this.anchor.setValues(limb.anchor[0], limb.anchor[1], limb.anchor[2]);
        this.origin.setValues(limb.origin[0], limb.origin[1], limb.origin[2]);
        this.color.setValues(limb.color[0], limb.color[1], limb.color[2]);
        this.opacity.trackpad.setValue(limb.opacity);
        this.mirror.button.setIsChecked(limb.mirror);
        this.lighting.button.setIsChecked(limb.lighting);
        this.shading.button.setIsChecked(limb.shading);
        this.smooth.button.setIsChecked(limb.smooth);
        this.is3D.button.setIsChecked(limb.is3D);

        this.holding.button.setValue(limb.holding.ordinal());
        this.slot.button.setValue(limb.slot.ordinal());
        this.hold.button.setIsChecked(limb.hold);
        this.swiping.button.setIsChecked(limb.swiping);
        this.looking.button.setIsChecked(limb.looking);
        this.swinging.button.setIsChecked(limb.swinging);
        this.idle.button.setIsChecked(limb.idle);
        this.invert.button.setIsChecked(limb.invert);
        this.wheel.button.setIsChecked(limb.wheel);
        this.wing.button.setIsChecked(limb.wing);
    }

    @Override
    public void resize(int width, int height)
    {
        if (this.resizer().h.unit == Measure.RELATIVE)
        {
            this.limbList.resizer().x(0).y(240).w(1, 0).h(1, -240);
            this.renameLimb.resizer().y(210);
            this.scroll.resizer().h(240 - 50);
            this.parentLimb.resizer().y(210);
        }
        else
        {
            this.limbList.resizer().x(1, -100).y(20).w(100).h(1, -20);
            this.renameLimb.resizer().y(1, -30);
            this.parentLimb.resizer().y(1, -30);
            this.scroll.resizer().h(1, -50);
            this.scroll.resizer().h(1, -50);
        }

        this.scroll.scroll.scrollSize = this.wing.area.getY(1) - this.scroll.area.y + 10;

        super.resize(width, height);
    }

    @Override
    protected void drawLabels()
    {
        super.drawLabels();

        if (this.resizer().h.unit == Measure.RELATIVE)
        {
            this.limbList.area.draw(0x88000000);
        }
    }
}