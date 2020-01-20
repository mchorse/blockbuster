package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiMainPanel;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;

/**
 * First time? https://i.redd.it/2dbksvvj34121.jpg
 */
public class GuiFirstTime extends GuiElement
{
	public GuiButtonElement<GuiButton> close;
	public GuiButtonElement<GuiButton> tutorial;
	public GuiButtonElement<GuiButton> youtube;
	public GuiButtonElement<GuiButton> discord;
	public GuiButtonElement<GuiButton> twitter;

	private String title;
	private List<String> welcome;
	private List<String> social;

	private final GuiDashboard dashboard;
	private final Overlay overlay;

	public GuiFirstTime(Minecraft mc, GuiDashboard dashboard, Overlay overlay)
	{
		super(mc);

		this.dashboard = dashboard;
		this.overlay = overlay;

		this.createChildren();

		this.close = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.done"), (button) -> this.close());
		this.tutorial = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.main.tutorial"), (button) -> GuiMainPanel.openWebLink(Blockbuster.TUTORIAL_URL));
		this.discord = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.main.discord"), (button) -> GuiMainPanel.openWebLink(Blockbuster.DISCORD_URL));
		this.youtube = GuiButtonElement.button(mc, "YouTube", (button) -> GuiMainPanel.openWebLink(Blockbuster.CHANNEL_URL));
		this.twitter = GuiButtonElement.button(mc, "Twitter", (button) -> GuiMainPanel.openWebLink(Blockbuster.TWITTER_URL));

		this.tutorial.resizer().set(10, 0, 0, 20).parent(this.area).w(0.5F, -12);
		this.youtube.resizer().set(0, 0, 0, 20).parent(this.area).x(0.5F, 2).w(0.5F, -12);
		this.discord.resizer().set(10, 0, 0, 20).parent(this.area).w(0.5F, -12).y(1, -55);
		this.twitter.resizer().set(0, 0, 0, 20).parent(this.area).x(0.5F, 2).w(0.5F, -12).y(1, -55);
		this.close.resizer().set(10, 0, 0, 20).parent(this.area).w(1, -20).y(1, -30);

		this.children.add(this.tutorial, this.discord, this.youtube, this.twitter, this.close);

		this.title = I18n.format("blockbuster.gui.first_time.title");
		this.welcome = this.font.listFormattedStringToWidth(I18n.format("blockbuster.gui.first_time.welcome"), 180);
		this.social = this.font.listFormattedStringToWidth(I18n.format("blockbuster.gui.first_time.social"), 180);
	}

	private void close()
	{
		this.dashboard.elements.elements.remove(this.overlay);

		/* Don't show anymore this modal */
		Property property = Blockbuster.proxy.config.config.getCategory(Configuration.CATEGORY_GENERAL).get("show_first_time_modal");

		property.set(false);
		Blockbuster.proxy.forge.save();
		Blockbuster.proxy.config.reload();
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
	}

	@Override
	public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
	{
		final int lineHeight = 11;

		this.area.draw(0xff000000);

		/* Draw extra text */
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.area.getX(0.5F) - this.font.getStringWidth(this.title), this.area.y + 10, 0);
		GlStateManager.scale(2, 2, 2);

		this.font.drawStringWithShadow(this.title, 0, 0, 0xffffff);
		GlStateManager.popMatrix();

		/* Draw welcome paragraph */
		int y = this.area.y + 35;

		for (String label : this.welcome)
		{
			this.font.drawStringWithShadow(label, this.area.x + 10, y, 0xaaaaaa);
			y += lineHeight;
		}

		y += 5;

		/* Readjust buttons */
		this.tutorial.resizer().y(y - this.area.y);
		this.tutorial.resize(this.dashboard.width, this.dashboard.height);
		this.youtube.resizer().y(y - this.area.y);
		this.youtube.resize(this.dashboard.width, this.dashboard.height);

		/* Draw social paragraph */
		y = this.discord.area.y - 5 - this.social.size() * lineHeight;

		for (String label : this.social)
		{
			this.font.drawStringWithShadow(label, this.area.x + 10, y, 0xaaaaaa);
			y += lineHeight;
		}

		super.draw(tooltip, mouseX, mouseY, partialTicks);
	}

	public static class Overlay extends GuiElement
	{
		public Overlay(Minecraft mc, GuiDashboard dashboard)
		{
			super(mc);

			GuiFirstTime firstTime = new GuiFirstTime(mc, dashboard, this);

			firstTime.resizer().set(0, 0, 200, 250).parent(this.area).x(0.5F, -100).y(0.5F, -125);

			this.createChildren();
			this.children.add(firstTime);
		}

		@Override
		public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
		{
			return super.mouseClicked(mouseX, mouseY, mouseButton) || this.isEnabled();
		}

		@Override
		public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
		{
			tooltip.set(null, null);
			this.area.draw(0x88000000);

			super.draw(tooltip, mouseX, mouseY, partialTicks);
		}
	}
}