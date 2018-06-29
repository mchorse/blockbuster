package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.IGuiLegacy;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMorphActionPanel extends GuiActionPanel<MorphAction> implements IGuiLegacy
{
    public GuiDashboard dashboard;
    public GuiButtonElement<GuiButton> pick;

    public GuiMorphActionPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc);

        this.title = "Morph action";
        this.dashboard = dashboard;
        this.pick = GuiButtonElement.button(mc, "Pick morph", (b) -> this.dashboard.morphs.hide(false));
        this.pick.resizer().parent(this.area).set(0, 5, 60, 20).x(0.5F, -30);

        this.children.add(this.pick);
    }

    @Override
    public void fill(MorphAction action)
    {
        super.fill(action);

        this.dashboard.morphs.setSelected(action.morph);
        this.dashboard.morphs.hide(true);
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.dashboard.morphs.updateRect(this.area.x, this.area.y, this.area.w, this.area.h);
        this.dashboard.morphs.setWorldAndResolution(this.mc, width, height);
    }

    @Override
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        boolean result = !this.dashboard.morphs.isHidden() && this.dashboard.morphs.isInside(mouseX, mouseY);

        this.dashboard.morphs.handleMouseInput();

        return result;
    }

    @Override
    public boolean handleKeyboardInput() throws IOException
    {
        this.dashboard.morphs.handleKeyboardInput();

        return !this.dashboard.morphs.isHidden();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        MorphCell cell = this.dashboard.morphs.getSelected();

        if (cell != null)
        {
            int x = this.area.getX(0.5F);
            int y = this.area.getY(0.8F);

            GuiScreen screen = this.mc.currentScreen;

            GuiUtils.scissor(this.area.x, this.area.y, this.area.w, this.area.h, screen.width, screen.height);
            cell.current().morph.renderOnScreen(this.mc.thePlayer, x, y, this.area.h / 3F, 1.0F);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        this.action.morph = cell == null ? null : cell.current().morph;

        super.draw(mouseX, mouseY, partialTicks);

        this.dashboard.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}