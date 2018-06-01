package mchorse.blockbuster.client.gui.dashboard.panels;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class GuiModelPanel extends GuiElement
{
    private TileEntityModel model;
    private TileEntityModel temp = new TileEntityModel(0);

    private GuiMorphsPopup morphs;

    public GuiModelPanel(Minecraft mc)
    {
        super(mc);

        this.createChildren();

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        this.morphs = new GuiMorphsPopup(6, null, Morphing.get(player));
        this.temp = new TileEntityModel();
    }

    public GuiModelPanel setModelBlock(TileEntityModel model)
    {
        this.model = model;
        this.temp.copyData(model);

        return this;
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.morphs.setWorldAndResolution(this.mc, width, height);
        this.morphs.updateRect(this.area.x, this.area.y, this.area.w, this.area.h);
        this.morphs.morphs.setSelected(this.model.morph);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        MorphCell cell = this.morphs.morphs.getSelected();

        if (cell != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, -40);
            cell.current().morph.renderOnScreen(this.mc.thePlayer, this.area.getX(0.5F), this.area.getY(0.65F), 30, 1.0F);
            GlStateManager.popMatrix();

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        this.morphs.morphs.setHidden(false);
        this.morphs.drawScreen(mouseX, mouseY, partialTicks);

        super.draw(mouseX, mouseY, partialTicks);
    }
}