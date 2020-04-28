package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiImageMorph extends GuiAbstractMorph<ImageMorph>
{
    public GuiImageMorphPanel general;

    public GuiImageMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiImageMorphPanel(mc, this);
        this.registerPanel(this.general, IKey.lang("blockbuster.morph.image"), Icons.GEAR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof ImageMorph;
    }

    public static class GuiImageMorphPanel extends GuiMorphPanel<ImageMorph, GuiImageMorph>
    {
        public GuiTexturePicker picker;
        public GuiButtonElement texture;
        public GuiTrackpadElement scale;
        public GuiToggleElement shaded;
        public GuiToggleElement lighting;
        public GuiToggleElement billboard;

        public GuiTrackpadElement left;
        public GuiTrackpadElement right;
        public GuiTrackpadElement top;
        public GuiTrackpadElement bottom;
        public GuiColorElement color;

        public GuiImageMorphPanel(Minecraft mc, GuiImageMorph editor)
        {
            super(mc, editor);

            this.texture = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_texture"), (b) ->
            {
                this.picker.refresh();
                this.picker.fill(this.morph.texture);
                this.picker.setVisible(true);
            });

            this.scale = new GuiTrackpadElement(mc, (value) ->
            {
                this.morph.scale = value;
            });
            this.scale.tooltip(IKey.lang("blockbuster.gui.model_block.scale"));

            this.shaded = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.shading"), false, (b) ->
            {
                this.morph.shaded = this.shaded.isToggled();
            });

            this.lighting = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.lighting"), false, (b) ->
            {
                this.morph.lighting = this.lighting.isToggled();
            });

            this.billboard = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.billboard"), false, (b) ->
            {
                this.morph.billboard = this.billboard.isToggled();
            });

            this.picker = new GuiTexturePicker(mc, (rl) ->
            {
                this.morph.texture = rl;
            });
            this.picker.setVisible(false);

            this.left = new GuiTrackpadElement(mc, (value) -> this.morph.cropping.x = value.intValue());
            this.left.tooltip(IKey.lang("blockbuster.gui.image.left"));
            this.left.integer();
            this.right = new GuiTrackpadElement(mc, (value) -> this.morph.cropping.w = value.intValue());
            this.right.tooltip(IKey.lang("blockbuster.gui.image.right"));
            this.right.integer();
            this.top = new GuiTrackpadElement(mc, (value) -> this.morph.cropping.y = value.intValue());
            this.top.tooltip(IKey.lang("blockbuster.gui.image.top"));
            this.top.integer();
            this.bottom = new GuiTrackpadElement(mc, (value) -> this.morph.cropping.h = value.intValue());
            this.bottom.tooltip(IKey.lang("blockbuster.gui.image.bottom"));
            this.bottom.integer();
            this.color = new GuiColorElement(mc, (value) -> this.morph.color = value);
            this.color.picker.editAlpha();

            this.texture.flex().relative(this.area).set(10, 10, 115, 20);
            this.scale.flex().relative(this.texture.resizer()).set(0, 25, 115, 20);
            this.shaded.flex().relative(this.scale.resizer()).set(0, 25, 80, 11);
            this.lighting.flex().relative(this.shaded.resizer()).set(0, 16, 80, 11);
            this.billboard.flex().relative(this.lighting.resizer()).set(0, 16, 80, 11);
            this.picker.flex().relative(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);

            this.left.flex().relative(this.billboard.resizer()).set(0, 11 + 30, 115, 20);
            this.right.flex().relative(this.left.resizer()).set(0, 25, 115, 20);
            this.top.flex().relative(this.right.resizer()).set(0, 25, 115, 20);
            this.bottom.flex().relative(this.top.resizer()).set(0, 25, 115, 20);
            this.color.flex().relative(this.bottom.resizer()).set(0, 25, 115, 20);

            this.add(this.texture, this.scale, this.shaded, this.lighting, this.billboard, this.left, this.right, this.top, this.bottom, this.color, this.picker);
        }

        @Override
        public void fillData(ImageMorph morph)
        {
            super.fillData(morph);

            this.scale.setValue(morph.scale);
            this.shaded.toggled(morph.shaded);
            this.lighting.toggled(morph.lighting);
            this.billboard.toggled(morph.billboard);

            this.left.setValue(morph.cropping.x);
            this.right.setValue(morph.cropping.w);
            this.top.setValue(morph.cropping.y);
            this.bottom.setValue(morph.cropping.h);

            this.color.picker.setColor(morph.color);
        }

        @Override
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.image.crop"), this.left.area.x, this.left.area.y - 12, 0xffffff);

            GifTexture.bindTexture(this.morph.texture);
            int w = this.morph.getWidth();
            int h = this.morph.getHeight();

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.image.dimensions", w, h), this.bottom.area.x, this.bottom.area.y + 25, 0xaaaaaa);

            super.draw(context);
        }
    }
}