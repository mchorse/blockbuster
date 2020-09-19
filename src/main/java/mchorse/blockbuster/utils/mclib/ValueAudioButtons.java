package mchorse.blockbuster.utils.mclib;

import mchorse.blockbuster.ClientProxy;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.config.values.ValueGUI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueAudioButtons extends ValueGUI
{
	public ValueAudioButtons(String id)
	{
		super(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, GuiConfig config)
	{
		GuiButtonElement resetAudio = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.reset_audio"), (button) -> ClientProxy.audio.reset());
		GuiButtonElement openAudio = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.main.open_audio"), (button) -> GuiUtils.openWebLink(ClientProxy.audio.folder.toURI()));

		return Arrays.asList(Elements.row(mc, 5, 0, 20, resetAudio, openAudio));
	}
}