package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.Blockbuster;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.util.List;

/**
 * First time? https://i.redd.it/2dbksvvj34121.jpg
 */
public class GuiFirstTime extends GuiElement
{
	public GuiButtonElement close;
	public GuiButtonElement tutorial;
	public GuiButtonElement youtube;
	public GuiButtonElement channel;
	public GuiButtonElement discord;
	public GuiButtonElement twitter;

	private String title;
	private List<String> welcome;
	private List<String> social;

	private final GuiDashboard dashboard;
	private final Overlay overlay;

	public static boolean shouldOpen()
	{
		return Blockbuster.generalFirstTime.get();
	}

	public GuiFirstTime(Minecraft mc, GuiDashboard dashboard, Overlay overlay)
	{
		super(mc);

		this.dashboard = dashboard;
		this.overlay = overlay;

		this.close = new GuiButtonElement(mc, I18n.format("blockbuster.gui.done"), (button) -> this.close());
		this.tutorial = new GuiButtonElement(mc, I18n.format("blockbuster.gui.main.tutorial"), (button) -> GuiUtils.openWebLink(Blockbuster.TUTORIAL_URL()));
		this.discord = new GuiButtonElement(mc, I18n.format("blockbuster.gui.main.discord"), (button) -> GuiUtils.openWebLink(Blockbuster.DISCORD_URL()));
		this.youtube = new GuiButtonElement(mc, "YouTube", (button) -> GuiUtils.openWebLink("https://www.youtube.com/c/McHorsesMods"));
		this.channel = new GuiButtonElement(mc, Blockbuster.langOrDefault("blockbuster.gui.first_time.channel", ""), (button) -> GuiUtils.openWebLink(Blockbuster.CHANNEL_URL()));
		this.twitter = new GuiButtonElement(mc, "Twitter", (button) -> GuiUtils.openWebLink(Blockbuster.TWITTER_URL()));

		this.tutorial.flex().set(10, 0, 0, 20).relative(this.area).w(0.5F, -12);
		this.youtube.flex().set(0, 0, 0, 20).relative(this.area).x(0.5F, 2).w(0.5F, -12);
		this.discord.flex().set(10, 0, 0, 20).relative(this.area).w(0.5F, -12).y(1, -55);
		this.twitter.flex().set(0, 0, 0, 20).relative(this.area).x(0.5F, 2).w(0.5F, -12).y(1, -55);
		this.close.flex().set(10, 0, 0, 20).relative(this.area).w(1, -20).y(1, -30);

		this.add(this.tutorial, this.discord, this.youtube, this.twitter, this.close);

		if (!this.channel.label.isEmpty())
		{
			this.tutorial.flex().set(10, 0, 0, 20).relative(this.area).w(0.33F, -10);
			this.channel.flex().set(0, 0, 0, 20).relative(this.area).x(0.5F, -30).w(60);
			this.youtube.flex().set(0, 0, 0, 20).relative(this.area).x(0.67F, 0).w(0.33F, -10);

			this.add(this.channel);
		}

		this.title = I18n.format("blockbuster.gui.first_time.title");
		this.welcome = this.font.listFormattedStringToWidth(I18n.format("blockbuster.gui.first_time.welcome"), 180);
		this.social = this.font.listFormattedStringToWidth(I18n.format("blockbuster.gui.first_time.social"), 180);
	}

	private void close()
	{
		this.dashboard.root.remove(this.overlay);

		/* Don't show anymore this modal */
		Blockbuster.generalFirstTime.set(true);
		Blockbuster.generalFirstTime.category.config.save();
	}

	@Override
	public void draw(GuiContext context)
	{
		final int lineHeight = 11;

		this.area.draw(0xff000000);

		/* Draw extra text */
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.area.mx() - this.font.getStringWidth(this.title), this.area.y + 10, 0);
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
		this.tutorial.flex().y(y - this.area.y);
		this.tutorial.resize();
		this.channel.flex().y(y - this.area.y);
		this.channel.resize();
		this.youtube.flex().y(y - this.area.y);
		this.youtube.resize();

		/* Draw social paragraph */
		y = this.discord.area.y - 5 - this.social.size() * lineHeight;

		for (String label : this.social)
		{
			this.font.drawStringWithShadow(label, this.area.x + 10, y, 0xaaaaaa);
			y += lineHeight;
		}

		super.draw(context);
	}

	public static class Overlay extends GuiElement
	{
		public Overlay(Minecraft mc, GuiDashboard dashboard)
		{
			super(mc);

			GuiFirstTime firstTime = new GuiFirstTime(mc, dashboard, this);

			firstTime.flex().set(0, 0, 200, 250).relative(this.area).x(0.5F, -100).y(0.5F, -125);

			this.add(firstTime);
			this.hideTooltip();
		}

		@Override
		public boolean mouseClicked(GuiContext context)
		{
			return super.mouseClicked(context) || this.isEnabled();
		}

		@Override
		public void draw(GuiContext context)
		{
			this.area.draw(0x88000000);

			super.draw(context);
		}
	}
}
