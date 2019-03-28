package mchorse.blockbuster_pack.client.gui;

import java.util.List;
import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.elements.GuiCreativeMorphsMenu;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.widgets.GuiInventory;
import mchorse.mclib.client.gui.widgets.GuiInventory.IInventoryPicker;
import mchorse.mclib.client.gui.widgets.GuiSlot;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.MorphBodyPart;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBodyPartEditor extends GuiElement implements IInventoryPicker
{
    public GuiCustomMorph parent;

    private GuiBodyPartListElement bodyParts;
    private GuiButtonElement<GuiButton> pickMorph;
    private GuiButtonElement<GuiCheckBox> useTarget;
    private GuiCreativeMorphs morphPicker;

    private GuiButtonElement<GuiButton> addPart;
    private GuiButtonElement<GuiButton> removePart;

    private GuiTrackpadElement tx;
    private GuiTrackpadElement ty;
    private GuiTrackpadElement tz;
    private GuiTrackpadElement sx;
    private GuiTrackpadElement sy;
    private GuiTrackpadElement sz;
    private GuiTrackpadElement rx;
    private GuiTrackpadElement ry;
    private GuiTrackpadElement rz;

    private GuiStringListElement limbs;
    private GuiElements<IGuiElement> editor = new GuiElements<IGuiElement>();

    private BodyPartManager parts;
    private BodyPart part;

    private GuiInventory inventory;
    private GuiSlot[] slots = new GuiSlot[6];
    private GuiSlot active;

    public GuiBodyPartEditor(Minecraft mc, GuiCustomMorph parent)
    {
        super(mc);

        this.parent = parent;
        this.createChildren();

        this.tx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.part.part.translate[0] = value);
        this.ty = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.part.part.translate[1] = value);
        this.tz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.part.part.translate[2] = value);
        this.sx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.part.part.scale[0] = value);
        this.sy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.part.part.scale[1] = value);
        this.sz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.part.part.scale[2] = value);
        this.rx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.part.part.rotate[0] = value);
        this.ry = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.part.part.rotate[1] = value);
        this.rz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.part.part.rotate[2] = value);

        this.tx.resizer().set(0, 35, 60, 20).parent(this.area).x(0.5F, -95).y(1, -105);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(65, 0, 60, 20).relative(this.tx.resizer());
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(65, 0, 60, 20).relative(this.sx.resizer());
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.limbs = new GuiStringListElement(mc, (str) ->
        {
            this.part.limb = str;
            this.parent.modelRenderer.limb = this.parent.getMorph().model.limbs.get(str);
        });

        this.bodyParts = new GuiBodyPartListElement(mc, (part) -> this.setPart(part));

        this.pickMorph = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.pick"), (b) ->
        {
            if (this.morphPicker == null)
            {
                IMorphing morphing = Morphing.get(this.mc.thePlayer);

                this.morphPicker = new GuiCreativeMorphsMenu(mc, 6, null, morphing);
                this.morphPicker.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
                this.morphPicker.callback = (morph) ->
                {
                    if (this.part != null) this.part.part.morph = morph;
                };

                GuiScreen screen = Minecraft.getMinecraft().currentScreen;

                this.morphPicker.resize(screen.width, screen.height);
                this.children.add(this.morphPicker);
            }

            this.children.unfocus();
            this.morphPicker.setSelected(this.part.part.morph);
            this.morphPicker.setVisible(true);
        });

        this.addPart = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.add"), (b) ->
        {
            BodyPart part = new BodyPart();
            String limb = this.limbs.getCurrent();

            part.part = new MorphBodyPart();
            part.init();

            if (limb != null)
            {
                part.limb = limb;
            }

            this.parts.parts.add(part);
            this.bodyParts.update();
            this.bodyParts.setCurrent(part);
            this.part = part;
            this.setPart(part);
        });

        this.removePart = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.remove"), (b) ->
        {
            if (this.part == null)
            {
                return;
            }

            List<BodyPart> parts = this.parts.parts;
            int index = parts.indexOf(this.part);

            if (index != -1)
            {
                parts.remove(this.part);
                this.bodyParts.update();
                index--;

                if (parts.size() >= 1)
                {
                    this.setPart(parts.get(index >= 0 ? index : 0));
                }
                else
                {
                    this.setPart(null);
                }
            }
        });

        this.useTarget = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.builder.use_target"), false, (b) ->
        {
            if (this.part != null) this.part.part.useTarget = b.button.isChecked();
        });

        this.limbs.resizer().parent(this.area).set(0, 50, 105, 90).x(1, -115).h(1, -105);
        this.pickMorph.resizer().parent(this.area).set(0, 10, 105, 20).x(1, -115);
        this.addPart.resizer().parent(this.area).set(10, 10, 50, 20);
        this.removePart.resizer().relative(this.addPart.resizer()).set(55, 0, 50, 20);
        this.bodyParts.resizer().parent(this.area).set(10, 50, 105, 0).h(1, -85);
        this.useTarget.resizer().parent(this.area).set(0, 0, 60, 11).x(1, -115).y(1, -49);

        this.editor.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.pickMorph, this.useTarget);
        this.children.add(this.addPart, this.removePart, this.bodyParts);
        this.children.add(this.editor);

        /* Inventory */
        this.inventory = new GuiInventory(this, mc.thePlayer);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlot(i);
        }
    }

    @Override
    public void pickItem(GuiInventory inventory, ItemStack stack)
    {
        if (this.active != null)
        {
            this.active.stack = stack == null ? null : stack.copy();
            this.part.part.slots[this.active.slot] = this.active.stack;
            this.inventory.visible = false;
            this.part.part.updateEntity();
        }
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i].update(this.area.getX(0.5F) + 30 * i - 90, this.area.y + 10);
        }

        this.inventory.update(this.area.getX(0.5F), this.area.getY(0.5F) - 40);

        if (this.morphPicker != null)
        {
            this.morphPicker.setPerRow((int) Math.ceil(this.morphPicker.area.w / 50.0F));
        }
    }

    public void startEditing(CustomMorph custom)
    {
        this.inventory.player = this.mc.thePlayer;
        this.parts = custom.parts;

        this.limbs.clear();
        this.limbs.add(custom.model.limbs.keySet());
        this.limbs.sort();

        this.bodyParts.setList(custom.parts.parts);
        this.bodyParts.update();
        this.setPart(custom.parts.parts.isEmpty() ? null : custom.parts.parts.get(0));
    }

    public void setupBodyEditor()
    {
        CustomMorph morph = this.parent.getMorph();

        this.bodyParts.update();
        this.setPart(morph.parts.parts.isEmpty() ? null : morph.parts.parts.get(0));
    }

    private void setPart(BodyPart part)
    {
        this.part = part;
        this.editor.setVisible(part != null);

        if (this.part != null)
        {
            this.fillBodyPart(part.part);
            this.limbs.setCurrent(part.limb);
            this.bodyParts.setCurrent(part);
            this.parent.modelRenderer.limb = this.parent.getMorph().model.limbs.get(part.limb);
        }
    }

    public void fillBodyPart(MorphBodyPart part)
    {
        if (part != null)
        {
            this.tx.trackpad.setValue(part.translate[0]);
            this.ty.trackpad.setValue(part.translate[1]);
            this.tz.trackpad.setValue(part.translate[2]);

            this.sx.trackpad.setValue(part.scale[0]);
            this.sy.trackpad.setValue(part.scale[1]);
            this.sz.trackpad.setValue(part.scale[2]);

            this.rx.trackpad.setValue(part.rotate[0]);
            this.ry.trackpad.setValue(part.rotate[1]);
            this.rz.trackpad.setValue(part.rotate[2]);

            this.useTarget.button.setIsChecked(part.useTarget);

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].stack = part.slots[i];
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        this.inventory.mouseClicked(mouseX, mouseY, mouseButton);
        this.active = null;

        for (GuiSlot slot : this.slots)
        {
            if (slot.area.isInside(mouseX, mouseY))
            {
                this.active = slot;
                this.inventory.visible = true;
            }
        }

        if (this.active != null || (this.inventory.visible && this.inventory.area.isInside(mouseX, mouseY)))
        {
            return true;
        }

        return false;
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        for (GuiSlot slot : this.slots)
        {
            slot.draw(mouseX, mouseY, partialTicks);
        }

        if (this.active != null)
        {
            Area a = this.active.area;

            Gui.drawRect(a.x, a.y, a.x + a.w, a.y + a.h, 0x880088ff);
        }

        this.inventory.draw(mouseX, mouseY, partialTicks);

        Gui.drawRect(this.bodyParts.area.x, this.bodyParts.area.y, this.bodyParts.area.getX(1), this.bodyParts.area.getY(1), 0x88000000);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.body_parts"), this.bodyParts.area.x, this.bodyParts.area.y - 12, 0xffffff);

        if (this.editor.isVisible())
        {
            Gui.drawRect(this.limbs.area.x, this.limbs.area.y, this.limbs.area.getX(1), this.limbs.area.getY(1), 0x88000000);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.limbs"), this.limbs.area.x, this.limbs.area.y - 12, 0xffffff);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    /**
     * Body part list which displays body parts 
     */
    public static class GuiBodyPartListElement extends GuiListElement<BodyPart>
    {
        public GuiBodyPartListElement(Minecraft mc, Consumer<BodyPart> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        public void sort()
        {}

        @Override
        public void drawElement(BodyPart element, int i, int x, int y, boolean hover)
        {
            if (this.current == i)
            {
                Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
            }

            String label = i + (!element.limb.isEmpty() ? " - " + element.limb : "");

            this.font.drawStringWithShadow(label, x + 4, y + 4, hover ? 16777120 : 0xffffff);
        }
    }
}