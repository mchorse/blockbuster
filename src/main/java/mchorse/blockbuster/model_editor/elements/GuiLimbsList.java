package mchorse.blockbuster.model_editor.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.client.gui.utils.GuiScrollPane;
import net.minecraft.client.gui.Gui;

public class GuiLimbsList extends GuiScrollPane
{
    /**
     * Span between two cells
     */
    private final int span = 20;

    /**
     * Currently editing model
     */
    public Model model;

    /**
     * Currently selected limb
     */
    public Model.Limb limb;

    /**
     * Limbs
     */
    public List<Model.Limb> limbs = new ArrayList<Model.Limb>();

    public ILimbPicker picker;

    public GuiLimbsList(ILimbPicker picker)
    {
        this.picker = picker;
    }

    public void reset()
    {
        this.model = null;
        this.limb = null;
        this.limbs.clear();
    }

    public void setModel(Model model)
    {
        this.model = model;
        this.scrollHeight = model.limbs.size() * this.span;
        this.limbs.clear();

        for (Model.Limb limb : model.limbs.values())
        {
            this.limbs.add(limb);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isInside(mouseX, mouseY) || mouseX > this.x + this.w - 8)
        {
            return;
        }

        int index = (mouseY - this.y + this.scrollY) / this.span;

        if (this.limbs.isEmpty() || index < 0 || index > this.limbs.size() - 1)
        {
            return;
        }

        this.limb = this.limbs.get(index);
        this.picker.pickLimb(this.limb);
    }

    /**
     * No background
     */
    @Override
    protected void drawBackground()
    {}

    @Override
    protected void drawPane(int mouseX, int mouseY, float partialTicks)
    {
        if (this.model == null)
        {
            return;
        }

        for (int i = 0, c = this.limbs.size(); i < c; i++)
        {
            Model.Limb limb = this.limbs.get(i);

            int x = this.x + 6;
            int y = this.y + i * this.span;

            int color = 0xffffffff;

            /* If hovered */
            if (mouseX >= this.x && mouseX <= this.x + this.w && mouseY + this.scrollY >= y && mouseY + this.scrollY < y + this.span)
            {
                color = 0xff999999;
            }

            /* If selected */
            if (limb == this.limb)
            {
                color = 0xff555555;
            }

            /* Label */
            this.fontRendererObj.drawStringWithShadow(limb.name, x, y + 6, color);

            /* Separator */
            if (i != c - 1)
            {
                Gui.drawRect(x - 5, y + this.span - 1, this.x + this.w - 1, y + this.span, 0x44ffffff);
            }
        }
    }

    public static interface ILimbPicker
    {
        public void pickLimb(Model.Limb limb);
    }
}