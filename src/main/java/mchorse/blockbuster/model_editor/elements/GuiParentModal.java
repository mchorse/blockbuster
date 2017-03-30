package mchorse.blockbuster.model_editor.elements;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.model_editor.modal.GuiModal;
import mchorse.metamorph.api.models.Model;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiParentModal extends GuiModal
{
    public GuiParentsView parents;
    public GuiButton button;
    public int id;

    public GuiParentModal(int id, Model.Limb current, Model model, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);

        List<String> limbs = new ArrayList<String>();

        limbs.add(I18n.format("blockbuster.gui.me.none"));
        limbs.addAll(model.limbs.keySet());

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
        int x = this.parent.width / 2 + this.width / 2;
        int y = this.parent.height / 2 + this.height / 2;

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