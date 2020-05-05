package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster_pack.morphs.SnowstormMorph;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class GuiSnowstormMorph extends GuiAbstractMorph<SnowstormMorph>
{
	public GuiSnowstormMorph(Minecraft mc)
	{
		super(mc);
	}

	@Override
	public List<Label<NBTTagCompound>> getPresets(SnowstormMorph morph)
	{
		List<Label<NBTTagCompound>> labels = new ArrayList<Label<NBTTagCompound>>();

		for (String preset : Blockbuster.proxy.particles.presets.keySet())
		{
			NBTTagCompound tag = new NBTTagCompound();

			tag.setString("Scheme", preset);
			this.addPreset(morph, labels, preset, tag);
		}

		return labels;
	}
}