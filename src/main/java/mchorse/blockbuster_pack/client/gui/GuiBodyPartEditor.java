package mchorse.blockbuster_pack.client.gui;

import java.util.List;
import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.elements.GuiCreativeMorphsMenu;
import mchorse.blockbuster.client.gui.utils.GuiUtils;
import mchorse.blockbuster_pack.client.render.part.BodyPart;
import mchorse.blockbuster_pack.client.render.part.MorphBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBodyPartEditor extends GuiElement
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

    private BodyPart part;

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
                this.morphPicker.resizer().parent(this.area).set(10, 10, 0, 0).w(1, -10).h(1, -10);
                this.morphPicker.callback = (morph) ->
                {
                    if (this.part != null) this.part.part.morph = morph;
                };

                GuiScreen screen = Minecraft.getMinecraft().currentScreen;

                this.morphPicker.resize(screen.width, screen.height);
                this.children.add(this.morphPicker);
            }

            GuiUtils.unfocusAllTextFields(this.children);

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

            this.parent.getMorph().parts.add(part);
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

            List<BodyPart> parts = this.parent.getMorph().parts;
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
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        if (this.morphPicker != null)
        {
            this.morphPicker.setPerRow((int) Math.ceil(this.morphPicker.area.w / 50.0F));
        }
    }

    public void startEditing(CustomMorph custom)
    {
        this.limbs.clear();
        this.limbs.add(custom.model.limbs.keySet());
        this.limbs.sort();

        this.bodyParts.setList(custom.parts);
        this.bodyParts.update();
        this.setPart(custom.parts.isEmpty() ? null : custom.parts.get(0));
    }

    public void setupBodyEditor()
    {
        CustomMorph morph = this.parent.getMorph();

        this.bodyParts.update();
        this.setPart(morph.parts.isEmpty() ? null : morph.parts.get(0));
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
        }
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
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