package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.function.Consumer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Creative morphs GUI picker
 *
 * This class is responsible for controlling {@link GuiCreativeMorphs}.
 */
public class GuiMorphsPopup extends GuiScreen
{
    /* GUI fields */
    private GuiCreativeMorphs morphs;
    public Consumer<AbstractMorph> callback;

    /* Poser */
    private Area area = new Area();
    private GuiTooltip tooltip = new GuiTooltip();

    public GuiMorphsPopup(int perRow, AbstractMorph selected, IMorphing morphing)
    {
        this.morphs = new GuiCreativeMorphsMenu(Minecraft.getMinecraft(), perRow, selected, morphing);
        this.morphs.setVisible(false);
        this.morphs.shiftX = 8;
        this.morphs.callback = (morph) -> this.selectMorph(morph);
    }

    private void selectMorph(AbstractMorph morph)
    {
        if (this.callback != null)
        {
            this.callback.accept(morph);
        }
    }

    public MorphCell getSelected()
    {
        return this.morphs.getSelected();
    }

    public void setSelected(AbstractMorph morph)
    {
        this.morphs.setSelected(morph);

        /* The unknown morph that can't be found in the morph picker 
         * will get cloned, so we have to retrieve it */
        MorphCell cell = this.getSelected();

        this.selectMorph(cell == null ? null : cell.current().morph);
    }

    public void hide(boolean hide)
    {
        this.morphs.setVisible(!hide);
    }

    public boolean isHidden()
    {
        return !this.morphs.isVisible();
    }

    public void updateRect(int x, int y, int w, int h)
    {
        this.area.set(x, y, w, h);

        this.morphs.area.set(x, y, w, h);
        this.morphs.setPerRow((int) Math.ceil(w / 50.0F));
    }

    public boolean isInside(int x, int y)
    {
        return !this.isHidden() && this.area.isInside(x, y);
    }

    /* Input */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
        {
            this.hide(true);
        }
        else if (button.id == 2)
        {
            this.morphs.toggleEditMode();
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        /* Firing a mouse scroll event */
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int scroll = -Mouse.getEventDWheel();

        if (scroll == 0)
        {
            return;
        }

        if (this.morphs.isEnabled())
        {
            this.morphs.mouseScrolled(x, y, scroll);
        }
    }

    /**
     * This method is responsible for hiding this popup when clicked outside of
     * this popup.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.isHidden())
        {
            return;
        }

        if (!this.isInside(mouseX, mouseY))
        {
            this.morphs.setVisible(false);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.morphs.isEnabled())
        {
            this.morphs.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.isHidden())
        {
            return;
        }

        if (this.morphs.isEnabled())
        {
            this.morphs.mouseReleased(mouseX, mouseY, state);
        }
    }

    /**
     * This method is responsible for scrolling morphs, typing in the search
     * bar and finally it also responsible for setting up filter for morphs.
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.isHidden())
        {
            return;
        }

        if (keyCode == 1)
        {
            if (this.morphs.search.field.isFocused())
            {
                this.morphs.search.field.setFocused(false);
            }
            else
            {
                this.hide(true);
            }
        }

        if (this.morphs.isEnabled())
        {
            this.morphs.keyTyped(typedChar, keyCode);
        }
    }

    /* GUI */

    /**
     * Initiate the search bar
     */
    @Override
    public void initGui()
    {
        this.morphs.resize(this.width, this.height);
    }

    /* Rendering */

    /**
     * Render popup
     *
     * This popup won't be rendered if the morphs picker is hidden.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.isHidden())
        {
            return;
        }

        this.tooltip.set(null, null);
        this.morphs.draw(this.tooltip, mouseX, mouseY, partialTicks);

        MorphCell cell = this.morphs.getSelected();

        if (cell != null && !this.morphs.isEditMode())
        {
            int width = Math.max(this.fontRenderer.getStringWidth(cell.current().name), this.fontRenderer.getStringWidth(cell.current().morph.name)) + 6;
            int center = this.area.getX(0.5F);
            int y = this.area.y + 40;

            Gui.drawRect(center - width / 2, y - 4, center + width / 2, y + 24, 0x88000000);

            this.drawCenteredString(this.fontRenderer, cell.current().name, center, y, 0xffffff);
            this.drawCenteredString(this.fontRenderer, cell.current().morph.name, center, y + 14, 0x888888);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Creative morph menu, but with a close button 
     */
    public static class GuiCreativeMorphsMenu extends GuiCreativeMorphs
    {
        private GuiButtonElement<GuiButton> close;

        public GuiCreativeMorphsMenu(Minecraft mc, int perRow, AbstractMorph selected, IMorphing morphing)
        {
            super(mc, perRow, selected, morphing);

            this.close = GuiButtonElement.button(mc, "X", (b) -> this.setVisible(false));
            this.close.resizer().parent(this.area).set(10, 10, 20, 20);
            this.children.add(this.close);

            this.search.resizer().set(35, 10, 0, 20).w(1, -130);
        }

        @Override
        public void toggleEditMode()
        {
            super.toggleEditMode();

            this.close.setVisible(this.editor.delegate == null);
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0xaa000000);

            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }
}