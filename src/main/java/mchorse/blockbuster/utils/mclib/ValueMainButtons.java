package mchorse.blockbuster.utils.mclib;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.config.values.ValueGUI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ValueMainButtons extends ValueGUI
{
    public ValueMainButtons(String id)
    {
        super(id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel config)
    {
        GuiButtonElement wiki = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.wiki"), (button) -> GuiUtils.openWebLink(Blockbuster.WIKI_URL()));
        GuiButtonElement discord = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.discord"), (button) -> GuiUtils.openWebLink(Blockbuster.DISCORD_URL()));
        GuiButtonElement tutorial = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.tutorial"), (button) -> GuiUtils.openWebLink(Blockbuster.TUTORIAL_URL()));
        GuiButtonElement models = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.models"), (button) -> GuiUtils.openFolder(new File(ClientProxy.configFile, "models").getAbsolutePath()));
        GuiButtonElement skins = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.skins"), (button) -> GuiUtils.openFolder(ClientProxy.skinsFolder.getAbsolutePath()));
        GuiIconElement youtube = new GuiIconElement(mc, BBIcons.YOUTUBE, (button) -> GuiUtils.openWebLink(Blockbuster.CHANNEL_URL()));
        GuiIconElement twitter = new GuiIconElement(mc, BBIcons.TWITTER, (button) -> GuiUtils.openWebLink(Blockbuster.TWITTER_URL()));

        GuiElement first = Elements.row(mc, 5, 0, 20, models, skins);
        GuiElement second = Elements.row(mc, 5, 0, 20, tutorial, wiki);
        GuiElement third = Elements.row(mc, 5, 0, 20, discord, twitter, youtube);

        return Arrays.asList(first, second, third);
    }
}