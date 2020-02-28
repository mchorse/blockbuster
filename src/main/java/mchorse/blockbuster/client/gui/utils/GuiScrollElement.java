package mchorse.blockbuster.client.gui.utils;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.ScrollArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 * TODO: transfer to McLib
 */
public abstract class GuiScrollElement extends GuiElement
{
	public ScrollArea scroll = new ScrollArea(0);

	public GuiScrollElement(Minecraft mc) {
		super(mc);
		this.createChildren();
	}

	public void resize(int width, int height)
	{
		super.resize(width, height);
		this.scroll.copy(this.area);
		this.scroll.clamp();
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		if (!this.area.isInside(mouseX, mouseY))
		{
			return false;
		}

		return super.mouseClicked(mouseX, mouseY + this.scroll.scroll, mouseButton) || this.scroll.mouseClicked(mouseX, mouseY);
	}

	public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
	{
		if (!this.area.isInside(mouseX, mouseY))
		{
			return false;
		}

		return super.mouseScrolled(mouseX, mouseY + this.scroll.scroll, scroll) || this.scroll.mouseScroll(mouseX, mouseY, scroll);
	}

	public void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY + this.scroll.scroll, state);
		this.scroll.mouseReleased(mouseX, mouseY);
	}

	public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
	{
		this.scroll.drag(mouseX, mouseY);
		mouseY += this.scroll.scroll;

		GuiScreen screen = this.mc.currentScreen;

		GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -this.scroll.scroll, 0);

		this.preDraw(tooltip, mouseX, mouseY, partialTicks);
		super.draw(tooltip, mouseX, mouseY, partialTicks);
		this.postDraw(tooltip, mouseX, mouseY, partialTicks);

		GlStateManager.popMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		this.scroll.drawScrollbar();
	}

	protected void preDraw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
	{}

	protected void postDraw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
	{}
}
