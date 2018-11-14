package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.utils.TextureLocation;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiImageMorph extends GuiAbstractMorph
{
    public GuiElements general = new GuiElements();

    public GuiTextElement texture;
    public GuiTrackpadElement scale;
    public GuiButtonElement<GuiCheckBox> shaded;

    public GuiButtonElement<GuiButton> toggleNbt;

    public GuiImageMorph(Minecraft mc)
    {
        super(mc);

        this.texture = new GuiTextElement(mc, 400, (str) ->
        {
            this.getMorph().texture = new TextureLocation(str);
        });

        this.scale = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.scale"), (value) ->
        {
            this.getMorph().scale = value;
        });

        this.shaded = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.limbs.shading"), false, (b) ->
        {
            this.getMorph().shaded = b.button.isChecked();
        });

        this.texture.resizer().parent(this.area).set(0, 50, 115, 20).x(1, -125);
        this.scale.resizer().relative(this.texture.resizer()).set(0, 25, 115, 20);
        this.shaded.resizer().relative(this.scale.resizer()).set(0, 25, 80, 11);

        this.toggleNbt = GuiButtonElement.button(mc, "NBT", (b) -> this.toggleNbt());
        this.toggleNbt.resizer().parent(this.area).set(0, 10, 40, 20).x(1, -50);

        this.data.setVisible(false);

        this.general.add(this.texture, this.scale, this.shaded);
        this.children.add(this.general, this.toggleNbt);
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
    public void startEdit(AbstractMorph morph)
    {
        super.startEdit(morph);

        ImageMorph image = this.getMorph();

        this.texture.setText(image.texture == null ? "" : image.texture.toString());
        this.scale.setValue(image.scale);
        this.shaded.button.setIsChecked(image.shaded);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (this.general.isVisible())
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.skin"), this.texture.area.x, this.texture.area.y - 12, 0xffffff);
        }
    }
}