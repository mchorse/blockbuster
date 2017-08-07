package mchorse.blockbuster.model_editor.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.api.models.Model.Limb;
import mchorse.metamorph.client.gui.utils.GuiScrollPane;
import net.minecraft.client.gui.Gui;

/**
 * Limb list GUI
 *
 * This method is responsible displaying available limbs in currently editing
 * custom model.
 */
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

    /**
     * Add a new limb
     */
    public void addLimb(String name)
    {
        if (name.isEmpty())
        {
            return;
        }

        Model.Limb limb = this.model.addLimb(name);

        this.limb = limb;
        this.limbs.add(limb);
        this.picker.pickLimb(limb);

        this.scrollHeight = this.limbs.size() * this.span;
        this.scrollTo(this.scrollHeight);
    }

    /**
     * Remove currently selected limb
     */
    public void removeLimb()
    {
        this.model.removeLimb(this.limb);

        this.limb = null;
        this.picker.pickLimb(null);
        this.constructLimbs();
    }

    public void setModel(Model model)
    {
        this.model = model;
        this.constructLimbs();
    }

    /**
     * Construct limbs
     *
     * This method is responsible for setting up limbs array for editing,
     * sorting it
     */
    private void constructLimbs()
    {
        this.scrollHeight = this.model.limbs.size() * this.span;
        this.limbs.clear();
        this.limbs.addAll(this.model.limbs.values());

        Collections.sort(this.limbs, new Comparator<Model.Limb>()
        {
            @Override
            public int compare(Limb a, Limb b)
            {
                return a.name.compareToIgnoreCase(b.name);
            }
        });
    }

    /**
     * Key typed
     *
     * This method is responsible for switching between the limbs in the limb
     * list sidebar.
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        boolean isUp = keyCode == Keyboard.KEY_UP;

        if (isUp || keyCode == Keyboard.KEY_DOWN)
        {
            int index = this.limbs.indexOf(this.limb);

            if (index != -1)
            {
                int length = this.limbs.size();

                index += isUp ? -1 : 1;
                index = index < 0 ? length - 1 : (index >= length ? 0 : index);

                this.limb = this.limbs.get(index);
                this.picker.pickLimb(this.limb);
            }
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
            this.limb = null;
            this.picker.pickLimb(null);

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
            this.fontRenderer.drawStringWithShadow(limb.name, x, y + 6, color);

            /* Separator */
            if (i != c - 1)
            {
                Gui.drawRect(x - 5, y + this.span - 1, this.x + this.w - 1, y + this.span, 0x44ffffff);
            }
        }
    }

    /**
     * Limb picker interface
     *
     * Basically this interface is used to notify the parent (or the current
     * displayed screen) that limb have been picked from this widget.
     */
    public static interface ILimbPicker
    {
        public void pickLimb(Model.Limb limb);
    }
}