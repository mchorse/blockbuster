package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.api.ModelLimb.Holding;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiListModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;

public class GuiModelLimbs extends GuiModelEditorTab
{
    private GuiIconElement addLimb;
    private GuiIconElement dupeLimb;
    private GuiIconElement removeLimb;
    private GuiIconElement renameLimb;
    private GuiIconElement parentLimb;

    private GuiStringListElement limbs;
    private GuiScrollElement scroll;

    /* First category */
    private GuiThreeElement size;
    private GuiTrackpadElement sizeOffset;
    private GuiTwoElement texture;
    private GuiThreeElement anchor;
    private GuiThreeElement origin;

    /* Second category */
    private GuiColorElement color;
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

        this.title = IKey.lang("blockbuster.gui.me.limbs.title");

        this.limbs = new GuiStringListElement(mc, (str) -> this.setLimb(str.get(0)));
        this.limbs.background().flex().relative(this).y(20).w(1F).h(100);

        this.scroll = new GuiScrollElement(mc);
        this.scroll.scroll.scrollSpeed = 15;
        this.scroll.flex().relative(this.limbs).y(1F).w(1F).hTo(this.area, 1F).column(5).vertical().stretch().scroll().height(20).padding(10);

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
            this.panel.limb.anchor[0] = values[0].floatValue();
            this.panel.limb.anchor[1] = values[1].floatValue();
            this.panel.limb.anchor[2] = values[2].floatValue();
            this.panel.rebuildModel();
        });
        this.origin = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.origin[0] = values[0].floatValue();
            this.panel.limb.origin[1] = values[1].floatValue();
            this.panel.limb.origin[2] = values[2].floatValue();
            this.panel.rebuildModel();
        });
        this.slot = new GuiCirculateElement(mc, (b) -> this.panel.limb.slot = ArmorSlot.values()[this.slot.getValue()]);
        this.slot.tooltip(IKey.lang("blockbuster.gui.me.limbs.slot"));
        this.hold = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.holding"), false, (b) -> this.panel.limb.hold = b.isToggled());

        for (ArmorSlot slot : ArmorSlot.values())
        {
            this.slot.addLabel(IKey.lang("blockbuster.gui.me.limbs.slots." + slot.name));
        }

        /* Second category */
        this.color = new GuiColorElement(mc, (eh) ->
        {
            Color color = this.color.picker.color;

            this.panel.limb.color[0] = color.r;
            this.panel.limb.color[1] = color.g;
            this.panel.limb.color[2] = color.b;
            this.panel.limb.opacity = color.a;
        });
        this.color.picker.editAlpha();
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
        this.holding.tooltip(IKey.lang("blockbuster.gui.me.limbs.hold"));
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

        this.scroll.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.size")).background(0x88000000), this.size);
        this.scroll.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.size_offset")).background(0x88000000), this.sizeOffset);
        this.scroll.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.texture"), 24).anchor(0, 1).background(0x88000000), this.texture, this.mirror);
        this.scroll.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.anchor"), 24).anchor(0, 1).background(0x88000000), this.anchor);
        this.scroll.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.origin")).background(0x88000000), this.origin);

        GuiElement appearance = new GuiElement(mc);

        appearance.flex().grid(5).items(2).resizes(true);
        appearance.add(this.lighting, this.shading);
        appearance.add(this.smooth, this.is3D);

        GuiElement animation = new GuiElement(mc);

        animation.flex().grid(5).items(2).resizes(true);
        animation.add(this.looking, this.idle);
        animation.add(this.swinging, this.invert);
        animation.add(this.swiping, this.hold);
        animation.add(this.wheel, this.wing);

        this.scroll.add(Elements.row(mc, 5, this.slot, this.holding));
        this.scroll.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.appearance"), 24).anchor(0, 1).background(0x88000000), appearance, this.color);
        this.scroll.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.animation"), 24).anchor(0, 1).background(0x88000000), animation);

        /* Buttons */
        this.addLimb = new GuiIconElement(mc, Icons.ADD, (b) -> this.addLimb());
        this.dupeLimb = new GuiIconElement(mc, Icons.DUPE, (b) -> this.dupeLimb());
        this.removeLimb = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeLimb());
        this.renameLimb = new GuiIconElement(mc, Icons.EDIT, (b) -> this.renameLimb());
        this.parentLimb = new GuiIconElement(mc, Icons.LIMB, (b) -> this.parentLimb());

        GuiElement sidebar = Elements.row(mc, 0, 0, 20, this.addLimb, this.dupeLimb, this.parentLimb, this.renameLimb, this.removeLimb);

        sidebar.flex().relative(this).x(1F).h(20).anchorX(1F).row(0).resize();

        this.add(sidebar, this.limbs, this.scroll);
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
            this.limbs.add(text);
            this.limbs.setCurrent(text);
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
        this.limbs.add(limb.name);
        this.limbs.setCurrent(limb.name);
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
            this.limbs.replace(text);
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
        this.limbs.setCurrent(str);
        this.fillLimbData(this.panel.limb);
    }

    public void fillData(Model model)
    {
        this.limbs.clear();
        this.limbs.add(model.limbs.keySet());
        this.limbs.sort();
    }

    public void fillLimbData(ModelLimb limb)
    {
        this.size.setValues(limb.size[0], limb.size[1], limb.size[2]);
        this.sizeOffset.setValue(limb.sizeOffset);
        this.texture.setValues(limb.texture[0], limb.texture[1]);
        this.anchor.setValues(limb.anchor[0], limb.anchor[1], limb.anchor[2]);
        this.origin.setValues(limb.origin[0], limb.origin[1], limb.origin[2]);
        this.color.picker.setColor(limb.color[0], limb.color[1], limb.color[2], limb.opacity);
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
}