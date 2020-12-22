package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiPoseTransformations;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAnimation;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiImageMorph extends GuiAbstractMorph<ImageMorph>
{
    public GuiImageMorphPanel general;

    public GuiImageMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiImageMorphPanel(mc, this);
        this.registerPanel(this.general, IKey.lang("blockbuster.morph.image"), Icons.GEAR);

        this.keys().register(IKey.lang("blockbuster.gui.builder.pick_texture"), Keyboard.KEY_P, () ->
        {
            if (!this.general.picker.hasParent())
            {
                this.general.texture.clickItself(GuiBase.getCurrent());
            }
        }).held(Keyboard.KEY_LSHIFT);
    }

    @Override
    protected GuiModelRenderer createMorphRenderer(Minecraft mc)
    {
        return new GuiMorphRenderer(mc)
        {
            @Override
            protected void drawUserModel(GuiContext context)
            {
                if (this.morph != null) {
                    MorphUtils.render(this.morph, this.entity, 0.0D, 0.0D, 0.0D, 0, context.partialTicks);
                }
            }
        };
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof ImageMorph;
    }

    @Override
    public List<Label<NBTTagCompound>> getPresets(ImageMorph morph)
    {
        List<Label<NBTTagCompound>> list = new ArrayList<Label<NBTTagCompound>>();

        GuiCustomMorph.addSkins(morph, list, "Texture", ClientProxy.tree.getByPath("image/skins", null));

        return list;
    }

    public static class GuiImageMorphPanel extends GuiMorphPanel<ImageMorph, GuiImageMorph>
    {
        public GuiPoseTransformations pose;

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
        public GuiToggleElement resizeCrop;
        public GuiColorElement color;

        public GuiTrackpadElement offsetX;
        public GuiTrackpadElement offsetY;
        public GuiTrackpadElement rotation;
        public GuiToggleElement keying;

        public GuiAnimation animation;

        public GuiImageMorphPanel(Minecraft mc, GuiImageMorph editor)
        {
            super(mc, editor);

            this.pose = new GuiPoseTransformations(mc);
            this.pose.flex().relative(this.area).set(0, 0, 190, 70).x(0.5F, -95).y(1, -75);
            this.texture = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_texture"), (b) ->
            {
                this.picker.refresh();
                this.picker.fill(this.morph.texture);

                this.add(this.picker);
                this.picker.resize();
            });

            this.shaded = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.shading"), false, (b) -> this.morph.shaded = b.isToggled());
            this.lighting = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.limbs.lighting"), false, (b) -> this.morph.lighting = b.isToggled());
            this.billboard = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.billboard"), false, (b) -> this.morph.billboard = b.isToggled());
            this.picker = new GuiTexturePicker(mc, (rl) -> this.morph.texture = rl);

            this.left = new GuiTrackpadElement(mc, (value) -> this.morph.crop.x = value.intValue());
            this.left.tooltip(IKey.lang("blockbuster.gui.image.left"));
            this.left.integer();
            this.right = new GuiTrackpadElement(mc, (value) -> this.morph.crop.z = value.intValue());
            this.right.tooltip(IKey.lang("blockbuster.gui.image.right"));
            this.right.integer();
            this.top = new GuiTrackpadElement(mc, (value) -> this.morph.crop.y = value.intValue());
            this.top.tooltip(IKey.lang("blockbuster.gui.image.top"));
            this.top.integer();
            this.bottom = new GuiTrackpadElement(mc, (value) -> this.morph.crop.w = value.intValue());
            this.bottom.tooltip(IKey.lang("blockbuster.gui.image.bottom"));
            this.bottom.integer();
            this.resizeCrop = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.image.resize_crop"), false, (b) -> this.morph.resizeCrop = b.isToggled());
            this.color = new GuiColorElement(mc, (value) -> this.morph.color = value).direction(Direction.TOP);
            this.color.picker.editAlpha();

            this.offsetX = new GuiTrackpadElement(mc, (value) -> this.morph.offsetX = value.floatValue());
            this.offsetX.tooltip(IKey.lang("blockbuster.gui.image.offset_x"));
            this.offsetY = new GuiTrackpadElement(mc, (value) -> this.morph.offsetY = value.floatValue());
            this.offsetY.tooltip(IKey.lang("blockbuster.gui.image.offset_y"));
            this.rotation = new GuiTrackpadElement(mc, (value) -> this.morph.rotation = value.floatValue());
            this.rotation.tooltip(IKey.lang("blockbuster.gui.image.rotation"));
            this.keying = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.image.keying"), false, (b) -> this.morph.keying = b.isToggled());
            this.keying.tooltip(IKey.lang("blockbuster.gui.image.keying_tooltip"), Direction.TOP);

            this.picker.flex().relative(this.area).wh(1F, 1F);

            GuiScrollElement column = new GuiScrollElement(mc);

            column.scroll.opposite = true;
            column.flex().relative(this).w(130).h(1F).column(5).vertical().stretch().scroll().height(20).padding(10);
            column.add(this.texture, this.scale, this.shaded, this.lighting, this.billboard, Elements.label(IKey.lang("blockbuster.gui.image.crop")), this.left, this.right, this.top, this.bottom, this.resizeCrop, this.color, this.offsetX, this.offsetY, this.rotation, this.keying);

            this.animation = new GuiAnimation(mc, true);
            this.animation.flex().relative(this).x(1F, -130).w(130);

            this.add(this.pose, column, this.animation);
        }

        @Override
        public void fillData(ImageMorph morph)
        {
            super.fillData(morph);

            this.picker.removeFromParent();

            this.pose.set(morph.pose);
            this.shaded.toggled(morph.shaded);
            this.lighting.toggled(morph.lighting);
            this.billboard.toggled(morph.billboard);

            this.left.setValue(morph.crop.x);
            this.right.setValue(morph.crop.z);
            this.top.setValue(morph.crop.y);
            this.bottom.setValue(morph.crop.w);
            this.resizeCrop.toggled(morph.resizeCrop);

            this.color.picker.setColor(morph.color);
            this.offsetX.setValue(morph.offsetX);
            this.offsetY.setValue(morph.offsetY);
            this.keying.toggled(morph.keying);

            this.animation.fill(morph.animation);
        }

        @Override
        public void finishEditing()
        {
            this.picker.close();

            super.finishEditing();
        }

        @Override
        public void draw(GuiContext context)
        {
            GifTexture.bindTexture(this.morph.texture);
            int w = this.morph.getWidth();
            int h = this.morph.getHeight();
            String label = I18n.format("blockbuster.gui.image.dimensions", w, h);

            this.font.drawStringWithShadow(label, this.area.x(0.5F, this.font.getStringWidth(label)), this.area.y + 16, 0xaaaaaa);

            super.draw(context);
        }
    }
}