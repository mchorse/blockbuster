package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.function.Consumer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
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
    public GuiButton poses;
    private GuiCreativeMorphs morphs;
    public Consumer<AbstractMorph> callback;

    /* Poser */
    private Area area = new Area();
    private boolean hidden = true;
    private GuiTooltip tooltip = new GuiTooltip();

    public GuiMorphsPopup(int perRow, AbstractMorph selected, IMorphing morphing)
    {
        this.morphs = new GuiCreativeMorphs(Minecraft.getMinecraft(), perRow, selected, morphing);
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
        this.hidden = hide;
        this.morphs.setVisible(!hide);
    }

    public boolean isHidden()
    {
        return this.hidden;
    }

    public void updateRect(int x, int y, int w, int h)
    {
        this.area.set(x, y, w, h);

        this.morphs.area.set(x, y + 25, w, h - 25);
        this.morphs.setPerRow((int) Math.ceil(w / 54.0F));
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

        this.search.mouseClicked(mouseX, mouseY, mouseButton);
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
            if (this.search.isFocused())
            {
                this.search.setFocused(false);
            }
            else
            {
                this.hide(true);
            }
        }

        if (this.search.isFocused())
        {
            this.search.textboxKeyTyped(typedChar, keyCode);
            this.morphs.setFilter(this.search.getText());
        }
        else if (this.morphs.isEnabled())
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
        this.search = new GuiTextField(0, this.fontRendererObj, this.area.x + 61 - 3, this.area.y + 4, this.area.w - 87 - 65, 18);
        this.close = new GuiButton(1, this.area.x + this.area.w - 23, this.area.y + 3, 20, 20, "X");
        this.poses = new GuiButton(2, this.area.x + this.area.w - 23 - 65, this.area.y + 3, 60, 20, I18n.format("blockbuster.gui.morphs.pose"));

        this.buttonList.clear();
        this.buttonList.add(this.close);
        this.buttonList.add(this.poses);

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

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0xcc000000);

        this.tooltip.set(null, null);
        this.morphs.draw(this.tooltip, mouseX, mouseY, partialTicks);

        MorphCell cell = this.morphs.getSelected();

        if (cell != null && !this.morphs.isEditMode())
        {
            int width = Math.max(this.fontRendererObj.getStringWidth(cell.current().name), this.fontRendererObj.getStringWidth(cell.current().morph.name)) + 6;
            int center = this.area.getX(0.5F);
            int y = this.area.y + 34;

            Gui.drawRect(center - width / 2, y - 4, center + width / 2, y + 24, 0xcc000000);

            this.drawCenteredString(fontRendererObj, cell.current().name, center, y, 0xffffff);
            this.drawCenteredString(fontRendererObj, cell.current().morph.name, center, y + 14, 0x888888);
        }

        this.fontRendererObj.drawStringWithShadow(I18n.format("blockbuster.gui.search"), this.area.x + 9, this.area.y + 9, 0xffffffff);
        this.search.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}