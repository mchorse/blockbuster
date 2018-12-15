package mchorse.blockbuster.client.gui.elements;

import org.lwjgl.opengl.GL11;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

/**
 * Creative morph menu, but with a close button 
 */
public class GuiCreativeMorphsMenu extends GuiCreativeMorphs
{
    private GuiButtonElement<GuiButton> close;

    public GuiCreativeMorphsMenu(Minecraft mc, int perRow, AbstractMorph selected, IMorphing morphing)
    {
        super(mc, perRow, selected, morphing);

        this.close = GuiButtonElement.button(mc, "X", (b) ->
        {
            if (this.isEditMode())
            {
                this.setMorph(this.getSelected().current().morph);
            }

            this.setVisible(false);
        });

        this.close.resizer().parent(this.area).set(10, 10, 20, 20);
        this.children.add(this.close);

        this.search.resizer().set(35, 10, 0, 20).w(1, -130);

        this.setVisible(false);
        this.shiftX = 8;
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);
        int perRow = (int) Math.ceil(this.area.w / 50.0F);

        this.setPerRow(perRow == 0 ? 1 : perRow);
    }

    @Override
    public void setSelected(AbstractMorph morph)
    {
        super.setSelected(morph);

        /* The unknown morph that can't be found in the morph picker 
         * will get cloned, so we have to retrieve it */
        MorphCell cell = this.getSelected();

        this.setMorph(cell == null ? null : cell.current().morph);
    }

    @Override
    public void toggleEditMode()
    {
        super.toggleEditMode();

        this.close.setVisible(this.editor.delegate == null);
    }

    /* Don't let click event pass through the background... */

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton) || this.area.isInside(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return super.mouseScrolled(mouseX, mouseY, scroll) || this.area.isInside(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        tooltip.set(null, null);

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0xaa000000);

        MorphCell cell = this.getSelected();

        if (cell != null && !this.isEditMode())
        {
            int width = Math.max(this.font.getStringWidth(cell.current().name), this.font.getStringWidth(cell.current().morph.name)) + 6;
            int center = this.area.getX(0.5F);
            int y = this.area.y + 40;

            Gui.drawRect(center - width / 2, y - 4, center + width / 2, y + 24, 0x88000000);

            this.drawCenteredString(this.font, cell.current().name, center, y, 0xffffff);
            this.drawCenteredString(this.font, cell.current().morph.name, center, y + 14, 0x888888);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}