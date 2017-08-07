package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

/**
 * Creative morphs GUI picker
 *
 * This class is responsible for controlling {@link GuiCreativeMorphs}.
 */
public class GuiMorphsPopup extends GuiScreen
{
    /* GUI fields */
    public GuiTextField search;
    public GuiButton close;
    public GuiCreativeMorphs morphs;

    /* Area rect */
    protected int x;
    protected int y;
    protected int w;
    protected int h;

    public GuiMorphsPopup(int perRow, AbstractMorph selected, IMorphing morphing)
    {
        this.morphs = new GuiCreativeMorphs(perRow, selected, morphing);
        this.morphs.setScrollSpeed(3);
        this.morphs.setHidden(true);
        this.morphs.shiftX = 8;
    }

    public void updateRect(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.morphs.updateRect(x, y + 25, w, h - 25);
    }

    public boolean isInside(int x, int y)
    {
        return !this.morphs.getHidden() && x >= this.x && this.x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
    }

    /* Input */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
        {
            this.morphs.setHidden(true);
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.morphs.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.morphs.handleMouseInput();
    }

    /**
     * This method is responsible for hiding this popup when clicked outside of
     * this popup.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.morphs.getHidden())
        {
            return;
        }

        if (!this.isInside(mouseX, mouseY))
        {
            this.morphs.setHidden(true);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.search.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * This method is responsible for scrolling morphs, typing in the search
     * bar and finally it also responsible for setting up filter for morphs.
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.morphs.getHidden())
        {
            return;
        }

        super.keyTyped(typedChar, keyCode);

        this.search.textboxKeyTyped(typedChar, keyCode);

        if (this.search.isFocused())
        {
            this.morphs.setFilter(this.search.getText());
        }
        else
        {
            if (keyCode == Keyboard.KEY_DOWN)
            {
                this.morphs.scrollBy(30);
            }
            else if (keyCode == Keyboard.KEY_UP)
            {
                this.morphs.scrollBy(-30);
            }
            else if (keyCode == Keyboard.KEY_LEFT)
            {
                this.morphs.scrollTo(0);
            }
            else if (keyCode == Keyboard.KEY_RIGHT)
            {
                this.morphs.scrollTo(this.morphs.getHeight());
            }
        }
    }

    /* GUI */

    /**
     * Initiate the search bar
     */
    @Override
    public void initGui()
    {
        this.search = new GuiTextField(0, this.fontRenderer, this.x + 61 - 3, this.y + 4, this.w - 87, 18);
        this.close = new GuiButton(1, this.x + this.w - 23, this.y + 3, 20, 20, "X");

        this.buttonList.add(this.close);
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
        if (this.morphs.getHidden())
        {
            return;
        }

        Gui.drawRect(this.x, this.y, this.x + this.w, this.y + this.h, 0xcc000000);
        this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.search"), this.x + 9, this.y + 9, 0xffffffff);

        this.search.drawTextBox();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 120);
        this.morphs.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();

        MorphCell cell = this.morphs.getSelected();

        if (cell != null)
        {
            int width = Math.max(this.fontRenderer.getStringWidth(cell.name), this.fontRenderer.getStringWidth(cell.morph.name)) + 6;
            int center = this.x + this.w / 2;
            int y = this.y + this.h - 26;

            Gui.drawRect(center - width / 2, y - 4, center + width / 2, y + 24, 0xcc000000);

            this.drawCenteredString(fontRenderer, cell.name, center, y, 0xffffff);
            this.drawCenteredString(fontRenderer, cell.morph.name, center, y + 14, 0x888888);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}