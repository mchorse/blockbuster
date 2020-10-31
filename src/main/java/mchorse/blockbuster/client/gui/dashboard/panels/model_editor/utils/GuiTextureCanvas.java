package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelLimbs;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvas;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTextureCanvas extends GuiCanvas
{
	public GuiElement editor;
	public GuiTrackpadElement x;
	public GuiTrackpadElement y;
	public GuiIconElement close;

	public GuiModelLimbs panel;
	public int w;
	public int h;

	public GuiTextureCanvas(Minecraft mc, GuiModelLimbs panel)
	{
		super(mc);

		this.panel = panel;

		this.close = new GuiIconElement(mc, Icons.CLOSE, (b) -> this.toggleVisible());
		this.close.flex().relative(this).x(1F, -25).y(5);

		/* TODO: move to GuiCanvas */
		this.editor = new GuiElement(mc);
		this.editor.flex().relative(this).xy(1F, 1F).w(130).anchor(1F, 1F).column(5).stretch().vertical().padding(10);

		this.x = new GuiTrackpadElement(mc, (value) ->
		{
			this.panel.getPanel().limb.texture[0] = value.intValue();
			this.panel.getPanel().rebuildModel();
		});
		this.x.limit(0, 8192, true);

		this.y = new GuiTrackpadElement(mc, (value) ->
		{
			this.panel.getPanel().limb.texture[1] = value.intValue();
			this.panel.getPanel().rebuildModel();
		});
		this.y.limit(0, 8192, true);

		this.editor.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.texture")).background(0x88000000), this.x, this.y);
		this.add(this.editor, this.close);

		this.markContainer();
	}

	/* TODO: move to GuiCanvas */
	public void setSize(int w, int h)
	{
		this.w = w;
		this.h = h;

		this.scaleX.set(0, 2);
		this.scaleY.set(0, 2);
		this.scaleX.view(-this.w / 2, this.w / 2, this.area.w, 20);
		this.scaleY.view(-this.h / 2, this.h / 2, this.area.h, 20);

		double min = Math.min(this.scaleX.zoom, this.scaleY.zoom);

		this.scaleX.zoom = min;
		this.scaleY.zoom = min;
	}

	@Override
	protected void drawCanvas(GuiContext context)
	{
		this.area.draw(0xff2f2f2f);

		Area area = this.calculate(-this.w / 2, -this.h / 2, this.w / 2, this.h / 2);

		Gui.drawRect(area.x - 1, area.y - 1, area.ex() + 1, area.ey() + 1, 0xff181818);
		GlStateManager.color(1, 1, 1, 1);

		ResourceLocation location = this.panel.getPanel().modelRenderer.texture;

		if (location == null)
		{
			return;
		}

		int ox = (this.area.x - area.x) % 16;
		int oy = (this.area.y - area.y) % 16;

		Area processed = new Area();
		processed.copy(this.area);
		processed.offsetX(ox < 0 ? 16 + ox : ox);
		processed.offsetY(oy < 0 ? 16 + oy : oy);
		processed.clamp(area);
		Icons.CHECKBOARD.renderArea(area.x, area.y, area.w, area.h);

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		area = this.calculate(-this.w / 2, -this.h / 2, this.w / 2, this.h / 2);

		this.mc.renderEngine.bindTexture(location);
		GuiDraw.drawBillboard(area.x, area.y, 0, 0, area.w, area.h, area.w, area.h);

		ModelLimb limb = this.panel.getPanel().limb;
		int lx = limb.texture[0];
		int ly = limb.texture[1];
		int lw = limb.size[0];
		int lh = limb.size[1];
		int ld = limb.size[2];

		/* Top and bottom */
		area = this.calculateRelative(lx + ld, ly, lx + ld + lw, ly + ld);

		Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x5500ff00);

		area = this.calculateRelative(lx + ld + lw, ly, lx + ld + lw + lw, ly + ld);

		Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x5500ffff);

		/* Front and back */
		area = this.calculateRelative(lx + ld, ly + ld, lx + ld + lw, ly + ld + lh);

		Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x550000ff);

		area = this.calculateRelative(lx + ld * 2 + lw, ly + ld, lx + ld * 2 + lw * 2, ly + ld + lh);

		Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x55ff00ff);

		area = this.calculateRelative(lx, ly, lx + ld * 2 + lw * 2, ly + ld + lh);

		/* Left and right */
		area = this.calculateRelative(lx, ly + ld, lx + ld, ly + ld + lh);

		Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x55ff0000);

		area = this.calculateRelative(lx + ld + lw, ly + ld, lx + ld * 2 + lw, ly + ld + lh);

		Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x55ffff00);

		/* Outline */
		area = this.calculateRelative(lx, ly, lx + ld * 2 + lw * 2, ly + ld + lh);

		GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), 0xffff0000);

		GlStateManager.color(1F, 1F, 1F);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	}

	private Area calculateRelative(int a, int b, int c, int d)
	{
		return this.calculate(-this.w / 2 + a, -this.h / 2 + b, -this.w / 2 + c, -this.h / 2 + d);
	}

	/* TODO: move to GuiCanvas */
	private Area calculate(int a, int b, int c, int d)
	{
		int x1 = (int) Math.round(this.scaleX.to(a) + this.area.mx());
		int y1 = (int) Math.round(this.scaleY.to(b) + this.area.my());
		int x2 = (int) Math.round(this.scaleX.to(c) + this.area.mx());
		int y2 = (int) Math.round(this.scaleY.to(d) + this.area.my());

		int x = x1;
		int y = y1;
		int fw = x2 - x;
		int fh = y2 - y;

		Area.SHARED.set(x, y, fw, fh);

		return Area.SHARED;
	}
}