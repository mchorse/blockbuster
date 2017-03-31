package mchorse.blockbuster.model_editor.elements.modals;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.model_editor.elements.scrolls.GuiParentsView;
import mchorse.blockbuster.model_editor.modal.GuiModal;
import mchorse.metamorph.api.models.Model;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * Parent change modal
 *
 * This modal is responsible for displaying available limbs which are suitable
 * for being parent and ability to retrieve selected parent limb.
 */
public class GuiParentModal extends GuiModal
{
    public GuiParentsView parents;
    public GuiButton button;
    public int id;

    public GuiParentModal(int id, Model.Limb current, Model model, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);

        /* Construct limbs list */
        List<String> limbs = new ArrayList<String>();

        limbs.add(I18n.format("blockbuster.gui.me.none"));
        limbs.addAll(model.limbs.keySet());

        /* Remove currently selected limb from parents */
        int index = limbs.indexOf(current.name);

        if (index != -1)
        {
            limbs.remove(index);
        }

        /* Parents scroll view */
        this.parents = new GuiParentsView(limbs, parent);

        if (!current.parent.isEmpty())
        {
            this.parents.index = limbs.indexOf(current.parent);
        }

        this.height = 130;
        this.id = id;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.parents.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.parents.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void wheelScroll(int mouseX, int mouseY, int scroll)
    {
        this.parents.scrollMouse(scroll, mouseX, mouseY);
    }

    @Override
    public void initiate()
    {
        super.initiate();

        int x = this.x + this.width;
        int y = this.y + this.height;

        this.parents.updateRect(x - this.width + 10, y - this.height + 30, this.width - 20, this.height - 57);
        this.parents.initiate();

        this.button = new GuiButton(this.id, x - this.width + 10, y - 28, this.width - 20, 20, I18n.format("blockbuster.gui.done"));
        this.buttons.clear();
        this.buttons.add(this.button);
    }

    @Override
    public void drawModal(int mouseX, int mouseY, float partialTicks)
    {
        super.drawModal(mouseX, mouseY, partialTicks);

        this.parents.draw(mouseX, mouseY, partialTicks);
    }
}