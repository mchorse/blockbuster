package mchorse.blockbuster.model_editor.elements.modals;

import mchorse.blockbuster.model_editor.modal.GuiModal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiConfirmModal extends GuiModal
{
    private GuiButton cancel;
    private GuiButton ok;
    private int cancelId;
    private int okId;

    public GuiConfirmModal(int cancelId, int okId, GuiScreen parent, FontRenderer font)
    {
        super(parent, font);
        this.cancelId = cancelId;
        this.okId = okId;
    }

    @Override
    public void initiate()
    {
        super.initiate();

        int x = this.x + this.width - this.buttonWidth - 8;
        int x2 = this.x + 8;
        int y = this.y + this.height - 28;

        this.cancel = new GuiButton(this.cancelId, x, y, this.buttonWidth, 20, I18n.format("blockbuster.no"));
        this.ok = new GuiButton(this.okId, x2, y, this.buttonWidth, 20, I18n.format("blockbuster.yes"));

        this.buttons.clear();
        this.buttons.add(this.cancel);
        this.buttons.add(this.ok);
    }
}