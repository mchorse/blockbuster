package mchorse.blockbuster_pack.client.gui;

import java.util.List;
import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup.GuiCreativeMorphsMenu;
import mchorse.blockbuster_pack.client.render.part.MorphBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.CustomMorph.BodyPart;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBodyPartEditor extends GuiElement
{
    public GuiCustomMorph parent;

    private GuiBodyPartListElement bodyParts;
    private GuiButtonElement<GuiButton> pickMorph;
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
    private GuiElements editor = new GuiElements();

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

        this.tx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -70);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -135);
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -135 - 65);
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.limbs = new GuiStringListElement(mc, (str) ->
        {
            this.part.limb = str;
            this.parent.modelRenderer.limb = this.parent.getMorph().model.limbs.get(str);
        });

        this.bodyParts = new GuiBodyPartListElement(mc, (part) -> this.setPart(part));

        this.pickMorph = GuiButtonElement.button(mc, "Pick morph", (b) ->
        {
            if (this.morphPicker == null)
            {
                this.morphPicker = new GuiCreativeMorphsMenu(mc, 6, null, null);
                this.morphPicker.resizer().parent(this.area).set(20, 20, 0, 0).w(1, -40).h(1, -40);
                this.morphPicker.callback = (morph) ->
                {
                    this.part.part.morph = morph;
                };

                GuiScreen screen = Minecraft.getMinecraft().currentScreen;

                this.morphPicker.resize(screen.width, screen.height);
                this.children.add(this.morphPicker);
            }

            this.morphPicker.setSelected(this.part.part.morph);
            this.morphPicker.setVisible(true);
        });

        this.addPart = GuiButtonElement.button(mc, "Add", (b) ->
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

        this.removePart = GuiButtonElement.button(mc, "Remove", (b) ->
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

        this.limbs.resizer().parent(this.area).set(0, 110, 80, 90).x(1, -90).h(1, -120);
        this.pickMorph.resizer().parent(this.area).set(10, 75, 105, 20).y(1, -30);
        this.addPart.resizer().parent(this.area).set(10, 35, 50, 20);
        this.removePart.resizer().relative(this.addPart.resizer()).set(55, 0, 50, 20);
        this.bodyParts.resizer().parent(this.area).set(10, 60, 105, 0).h(1, -95);

        this.editor.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.pickMorph);
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
        }
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