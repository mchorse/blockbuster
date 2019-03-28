package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiImageMorph extends GuiAbstractMorph
{
    public GuiElements<GuiElement> general = new GuiElements<GuiElement>();

    public GuiTexturePicker picker;
    public GuiButtonElement<GuiButton> texture;
    public GuiTrackpadElement scale;
    public GuiButtonElement<GuiCheckBox> shaded;
    public GuiButtonElement<GuiCheckBox> lighting;
    public GuiButtonElement<GuiCheckBox> billboard;

    public GuiButtonElement<GuiButton> toggleNbt;

    public GuiImageMorph(Minecraft mc)
    {
        super(mc);

        this.texture = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.pick_texture"), (b) ->
        {
            this.picker.setVisible(true);
            this.picker.refresh();
        });

        this.scale = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.scale"), (value) ->
        {
            this.getMorph().scale = value;
        });

        this.shaded = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.shading"), false, (b) ->
        {
            this.getMorph().shaded = b.button.isChecked();
        });

        this.lighting = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.lighting"), false, (b) ->
        {
            this.getMorph().lighting = b.button.isChecked();
        });

        this.billboard = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.billboard"), false, (b) ->
        {
            this.getMorph().billboard = b.button.isChecked();
        });

        this.picker = new GuiTexturePicker(mc, (rl) ->
        {
            this.getMorph().texture = rl;
        });
        this.picker.setVisible(false);

        this.texture.resizer().parent(this.area).set(0, 50, 115, 20).x(1, -125);
        this.scale.resizer().relative(this.texture.resizer()).set(0, 25, 115, 20);
        this.shaded.resizer().relative(this.scale.resizer()).set(0, 25, 80, 11);
        this.lighting.resizer().relative(this.shaded.resizer()).set(0, 16, 80, 11);
        this.billboard.resizer().relative(this.lighting.resizer()).set(0, 16, 80, 11);
        this.picker.resizer().parent(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);

        this.toggleNbt = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.nbt"), (b) -> this.toggleNbt());
        this.toggleNbt.resizer().parent(this.area).set(0, 10, 40, 20).x(1, -50);

        this.data.setVisible(false);

        this.general.add(this.texture, this.scale, this.shaded, this.lighting, this.billboard);
        this.children.add(this.general, this.toggleNbt, this.picker);
    }

    private void toggleNbt()
    {
        if (this.data.isVisible())
        {
            this.general.setVisible(true);
            this.data.setVisible(false);
        }
        else
        {
            this.updateNBT();
            this.general.setVisible(false);
            this.data.setVisible(true);
        }
    }

    public ImageMorph getMorph()
    {
        return (ImageMorph) this.morph;
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof ImageMorph;
    }

    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.pushMatrix();
        super.drawMorph(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

    @Override
    public void startEdit(AbstractMorph morph)
    {
        super.startEdit(morph);

        ImageMorph image = this.getMorph();

        this.picker.fill(image.texture);
        this.picker.picker.sort();

        this.scale.setValue(image.scale);
        this.shaded.button.setIsChecked(image.shaded);
        this.lighting.button.setIsChecked(image.lighting);
        this.billboard.button.setIsChecked(image.billboard);
    }
}