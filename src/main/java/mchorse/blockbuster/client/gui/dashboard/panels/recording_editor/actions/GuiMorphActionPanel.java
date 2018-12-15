package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiMorphActionPanel extends GuiActionPanel<MorphAction>
{
    public GuiDashboard dashboard;
    public GuiButtonElement<GuiButton> pick;

    public GuiMorphActionPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc);

        this.dashboard = dashboard;
        this.pick = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.pick"), (b) -> this.dashboard.morphs.setVisible(true));
        this.pick.resizer().parent(this.area).set(0, 5, 60, 20).x(0.5F, -30);

        this.children.add(this.pick);
    }

    @Override
    public void setMorph(AbstractMorph morph)
    {
        this.action.morph = morph;
    }

    @Override
    public void fill(MorphAction action)
    {
        super.fill(action);

        this.dashboard.morphs.setSelected(action.morph);
    }

    @Override
    public void appear()
    {
        if (this.action != null)
        {
            this.fill(action);
        }
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.action.morph != null)
        {
            int x = this.area.getX(0.5F);
            int y = this.area.getY(0.8F);

            GuiScreen screen = this.mc.currentScreen;

            GuiUtils.scissor(this.area.x, this.area.y, this.area.w, this.area.h, screen.width, screen.height);
            this.action.morph.renderOnScreen(this.mc.player, x, y, this.area.h / 3F, 1.0F);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}