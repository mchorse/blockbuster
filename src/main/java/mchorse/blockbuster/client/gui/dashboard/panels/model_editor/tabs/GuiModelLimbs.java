package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.api.ModelLimb.Holding;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiListModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.resizers.Flex;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiModelLimbs extends GuiModelEditorTab
{
    private GuiIconElement addLimb;
    private GuiIconElement dupeLimb;
    private GuiIconElement removeLimb;
    private GuiButtonElement renameLimb;
    private GuiButtonElement parentLimb;

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
    private GuiToggleElement mirror;
    private GuiToggleElement lighting;
    private GuiToggleElement shading;
    private GuiToggleElement smooth;
    private GuiToggleElement is3D;

    private GuiCirculateElement holding;
    private GuiCirculateElement slot;
    private GuiToggleElement hold;
    private GuiToggleElement swiping;
    private GuiToggleElement looking;
    private GuiToggleElement swinging;
    private GuiToggleElement idle;
    private GuiToggleElement invert;
    private GuiToggleElement wheel;
    private GuiToggleElement wing;

    public GuiModelLimbs(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        final GuiModelLimbs limbs = this;

        this.title = IKey.lang("blockbuster.gui.me.limbs.title");

        this.scroll = new GuiScrollElement(mc)
        {
            @Override
            protected void preDraw(GuiContext context)
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
        this.scroll.flex().relative(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -50);

        this.limbList = new GuiStringListElement(mc, (str) -> this.setLimb(str.get(0)));
        this.limbList.flex().set(0, 20, 100, 0).relative(this.area).h(1, -20).x(1, -100);
        this.add(this.limbList);

        /* First category */
        this.size = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.size[0] = values[0].intValue();
            this.panel.limb.size[1] = values[1].intValue();
            this.panel.limb.size[2] = values[2].intValue();
            this.panel.rebuildModel();
        });
        this.size.setLimit(1, 8192, true);
        this.sizeOffset = new GuiTrackpadElement(mc, (value) ->
        {
            this.panel.limb.sizeOffset = value.floatValue();
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
        this.slot = new GuiCirculateElement(mc, (b) -> this.panel.limb.slot = ArmorSlot.values()[this.slot.getValue()]);
        this.slot.tooltip(IKey.lang("blockbuster.gui.me.limbs.slot"), Direction.LEFT);
        this.hold = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.holding"), false, (b) -> this.panel.limb.hold = b.isToggled());

        for (ArmorSlot slot : ArmorSlot.values())
        {
            this.slot.addLabel(IKey.lang("blockbuster.gui.me.limbs.slots." + slot.name));
        }

        this.size.flex().relative(this.scroll.area).set(10, 20, 120, 20);
        this.sizeOffset.flex().relative(this.size.resizer()).set(0, 35, 120, 20);
        this.texture.flex().relative(this.sizeOffset.resizer()).set(0, 35, 120, 20);
        this.anchor.flex().relative(this.texture.resizer()).set(0, 35, 120, 20);
        this.origin.flex().relative(this.anchor.resizer()).set(0, 35, 120, 20);
        this.slot.flex().relative(this.origin.resizer()).set(0, 22, 120, 20);
        this.hold.flex().relative(this.slot.resizer()).set(0, 25, 60, 11);

        /* Second category */
        this.color = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.color[0] = values[0];
            this.panel.limb.color[1] = values[1];
            this.panel.limb.color[2] = values[2];
        });
        this.color.setLimit(0, 1);
        this.opacity = new GuiTrackpadElement(mc, (value) -> this.panel.limb.opacity = value.floatValue());
        this.opacity.tooltip(IKey.lang("blockbuster.gui.me.limbs.opacity"));
        this.opacity.limit(0, 1);
        this.mirror = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.mirror"), false, (b) ->
        {
            this.panel.limb.mirror = b.isToggled();
            this.panel.rebuildModel();
        });
        this.lighting = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.lighting"), false, (b) -> this.panel.limb.lighting = b.isToggled());
        this.shading = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.shading"), false, (b) -> this.panel.limb.shading = b.isToggled());
        this.smooth = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.smooth"), false, (b) -> this.panel.limb.smooth = b.isToggled());
        this.is3D = new GuiToggleElement(mc, IKey.str("3D"), false, (b) -> this.panel.limb.is3D = b.isToggled());

        this.holding = new GuiCirculateElement(mc, (b) ->
        {
            this.panel.limb.holding = Holding.values()[this.holding.getValue()];
            this.panel.rebuildModel();
        });
        this.holding.tooltip(IKey.lang("blockbuster.gui.me.limbs.hold"), Direction.LEFT);
        this.holding.addLabel(IKey.lang("blockbuster.gui.me.limbs.none"));
        this.holding.addLabel(IKey.lang("blockbuster.gui.me.limbs.right"));
        this.holding.addLabel(IKey.lang("blockbuster.gui.me.limbs.left"));

        this.swiping = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.swiping"), false, (b) -> this.panel.limb.swiping = b.isToggled());
        this.looking = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.looking"), false, (b) -> this.panel.limb.looking = b.isToggled());
        this.swinging = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.swinging"), false, (b) -> this.panel.limb.swinging = b.isToggled());
        this.idle = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.idle"), false, (b) -> this.panel.limb.idle = b.isToggled());
        this.invert = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.invert"), false, (b) -> this.panel.limb.invert = b.isToggled());
        this.wheel = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.wheel"), false, (b) -> this.panel.limb.wheel = b.isToggled());
        this.wing = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.wing"), false, (b) -> this.panel.limb.wing = b.isToggled());

        this.color.flex().relative(this.hold.resizer()).set(0, 40, 120, 20);
        this.opacity.flex().relative(this.color.resizer()).set(0, 25, 120, 20);

        this.mirror.flex().relative(this.opacity.resizer()).set(0, 25, 60, 11);
        this.is3D.flex().relative(this.mirror.resizer()).set(0, 16, 60, 11);

        this.lighting.flex().relative(this.opacity.resizer()).set(60, 25, 60, 11);
        this.shading.flex().relative(this.lighting.resizer()).set(0, 16, 60, 11);
        this.smooth.flex().relative(this.shading.resizer()).set(0, 16, 60, 11);

        this.holding.flex().relative(this.is3D.resizer()).set(0, 25, 56, 20);

        this.swiping.flex().relative(this.idle.resizer()).set(0, -16, 60, 11);
        this.looking.flex().relative(this.holding.resizer()).set(0, 25, 60, 11);
        this.idle.flex().relative(this.looking.resizer()).set(60, 0, 60, 11);
        this.swinging.flex().relative(this.looking.resizer()).set(0, 16, 60, 11);
        this.invert.flex().relative(this.swinging.resizer()).set(60, 0, 60, 11);
        this.wheel.flex().relative(this.swinging.resizer()).set(0, 16, 60, 11);
        this.wing.flex().relative(this.wheel.resizer()).set(60, 0, 60, 11);

        this.add(this.scroll);
        this.scroll.add(this.size, this.sizeOffset, this.texture, this.anchor, this.origin, this.slot, this.hold);
        this.scroll.add(this.color, this.opacity, this.mirror, this.lighting, this.shading, this.smooth, this.is3D, this.holding, this.swiping, this.looking, this.swinging, this.idle, this.invert, this.wheel, this.wing);

        /* Buttons */
        this.addLimb = new GuiIconElement(mc, Icons.ADD, (b) -> this.addLimb());
        this.dupeLimb = new GuiIconElement(mc, Icons.DUPE, (b) -> this.dupeLimb());
        this.removeLimb = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeLimb());
        this.renameLimb = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.me.limbs.rename"), (b) -> this.renameLimb());
        this.parentLimb = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.me.limbs.parent"), (b) -> this.parentLimb());

        this.addLimb.flex().set(0, 2, 16, 16).relative(this.area).x(1, -58);
        this.dupeLimb.flex().set(20, 0, 16, 16).relative(this.addLimb.resizer());
        this.removeLimb.flex().set(20, 0, 16, 16).relative(this.dupeLimb.resizer());

        this.renameLimb.flex().set(10, 0, 0, 20).relative(this.area).w(0.5F, -13).y(1, -30);
        this.parentLimb.flex().set(0, 0, 0, 20).relative(this.area).x(0.5F, 3).w(0.5F, -13).y(1, -30);

        this.add(this.addLimb, this.dupeLimb, this.removeLimb, this.renameLimb, this.parentLimb);
        this.resize();
    }

    private void addLimb()
    {
        GuiModal.addFullModal(this, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(mc, IKey.lang("blockbuster.gui.me.limbs.new_limb"), this::addLimb);

            return modal.setValue(this.panel.limb.name);
        });
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
            GuiModal.addFullModal(this, () -> new GuiMessageModal(this.mc, IKey.lang("blockbuster.gui.me.limbs.last_limb")));
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
        GuiModal.addFullModal(this, () -> new GuiPromptModal(mc, IKey.lang("blockbuster.gui.me.limbs.rename_limb"), this::renameLimb).setValue(this.panel.limb.name));
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
        GuiModal.addFullModal(this, () ->
        {
            GuiListModal modal = new GuiListModal(mc, IKey.lang("blockbuster.gui.me.limbs.parent_limb"), this::parentLimb);

            return modal.addValues(this.panel.model.limbs.keySet()).setValue(this.panel.limb.parent);
        });
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
        this.opacity.setValue(limb.opacity);
        this.mirror.toggled(limb.mirror);
        this.lighting.toggled(limb.lighting);
        this.shading.toggled(limb.shading);
        this.smooth.toggled(limb.smooth);
        this.is3D.toggled(limb.is3D);

        this.holding.setValue(limb.holding.ordinal());
        this.slot.setValue(limb.slot.ordinal());
        this.hold.toggled(limb.hold);
        this.swiping.toggled(limb.swiping);
        this.looking.toggled(limb.looking);
        this.swinging.toggled(limb.swinging);
        this.idle.toggled(limb.idle);
        this.invert.toggled(limb.invert);
        this.wheel.toggled(limb.wheel);
        this.wing.toggled(limb.wing);
    }

    @Override
    public void resize()
    {
        if (this.flex().h.unit == Flex.Measure.RELATIVE)
        {
            this.limbList.flex().x(0).y(240).w(1, 0).h(1, -240);
            this.renameLimb.flex().y(210);
            this.scroll.flex().h(240 - 50);
            this.parentLimb.flex().y(210);
        }
        else
        {
            this.limbList.flex().x(1, -100).y(20).w(100).h(1, -20);
            this.renameLimb.flex().y(1, -30);
            this.parentLimb.flex().y(1, -30);
            this.scroll.flex().h(1, -50);
            this.scroll.flex().h(1, -50);
        }

        this.scroll.scroll.scrollSize = this.wing.area.ey() - this.scroll.area.y + 10;

        super.resize();
    }

    @Override
    protected void drawLabels()
    {
        super.drawLabels();

        if (this.flex().h.unit == Flex.Measure.RELATIVE)
        {
            this.limbList.area.draw(0x88000000);
        }
    }
}