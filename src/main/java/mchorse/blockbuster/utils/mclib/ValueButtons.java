package mchorse.blockbuster.utils.mclib;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.config.values.Value;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ValueButtons extends Value
{
	public ValueButtons(String id)
	{
		super(id);
	}

	@Override
	public void reset()
	{}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, GuiConfig config)
	{
		GuiButtonElement wiki = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.wiki"), (button) -> GuiUtils.openWebLink(Blockbuster.WIKI_URL()));
		GuiButtonElement discord = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.discord"), (button) -> GuiUtils.openWebLink(Blockbuster.DISCORD_URL()));
		GuiButtonElement tutorial = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.tutorial"), (button) -> GuiUtils.openWebLink(Blockbuster.TUTORIAL_URL()));
		GuiButtonElement models = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.models"), (button) -> GuiUtils.openWebLink(new File(ClientProxy.configFile, "models").toURI()));
		GuiIconElement youtube = new GuiIconElement(mc, BBIcons.YOUTUBE, (button) -> GuiUtils.openWebLink(Blockbuster.CHANNEL_URL()));
		GuiIconElement twitter = new GuiIconElement(mc, BBIcons.TWITTER, (button) -> GuiUtils.openWebLink(Blockbuster.TWITTER_URL()));

		GuiElement first = Elements.row(mc, 5, 0, 20, models, discord);
		GuiElement second = Elements.row(mc, 5, 0, 20, tutorial, wiki, twitter, youtube);

		return Arrays.asList(first, second);
	}

	@Override
	public void fromJSON(JsonElement jsonElement)
	{}

	@Override
	public JsonElement toJSON()
	{
		return JsonNull.INSTANCE;
	}
}