package mchorse.blockbuster.model_editor.elements;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.model_editor.modal.GuiScrollView;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class GuiPosesView extends GuiScrollView
{
    /**
     * Span between two cells
     */
    private final int span = 20;

    /**
     * Currently selected limb
     */
    public int index;

    /**
     * Limbs
     */
    public List<String> limbs = new ArrayList<String>();

    public GuiPosesView(GuiScreen parent)
    {
        super(parent);
    }

    public void setPoses(List<String> poses)
    {
        this.limbs.clear();
        this.limbs.addAll(poses);

        this.scrollHeight = this.span * this.limbs.size();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isInside(mouseX, mouseY) || mouseX > this.x + this.w - 8)
        {
            return;
        }

        int index = (mouseY - this.y + this.scrollY) / this.span;

        if (this.limbs.isEmpty() || index < 0 || index > this.limbs.size() - 1)
        {
            this.index = 0;

            return;
        }

        this.index = index;
    }

    public String getSelected()
    {
        return this.limbs.isEmpty() || this.index < 0 && this.index > this.limbs.size() ? "" : this.limbs.get(this.index);
    }

    public void setSelected(String selected)
    {
        this.index = this.limbs.indexOf(selected);
    }

    /**
     * No background
     */
    @Override
    protected void drawBackground()
    {}

    @Override
    protected void drawView(int mouseX, int mouseY, float partialTicks)
    {
        for (int i = 0, c = this.limbs.size(); i < c; i++)
        {
            String limb = this.limbs.get(i);

            int x = this.x + 6;
            int y = this.y + i * this.span;

            int color = 0xffffffff;

            /* If hovered */
            if (mouseX >= this.x && mouseX <= this.x + this.w && mouseY + this.scrollY >= y && mouseY + this.scrollY < y + this.span)
            {
                color = 0xff999999;
            }

            /* If selected */
            if (i == this.index)
            {
                color = 0xff555555;
            }

            /* Label */
            this.parent.mc.fontRendererObj.drawStringWithShadow(limb, x, y + 6, color);

            /* Separator */
            if (i != c - 1)
            {
                Gui.drawRect(x - 5, y + this.span - 1, this.x + this.w - 1, y + this.span, 0x44ffffff);
            }
        }
    }
}