package mchorse.blockbuster_pack.utils;

import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiInterpolationList;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Interpolation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAnimation extends GuiElement
{
	/* Animated poses */
	public GuiToggleElement animates;
	public GuiToggleElement ignored;
	public GuiTrackpadElement animationDuration;
	public GuiButtonElement pickInterpolation;
	public GuiListElement<Interpolation> interpolations;

	public Animation animation;

	public GuiAnimation(Minecraft mc)
	{
		super(mc);

		/* Animated poses */
		this.animates = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.animates"), false, (b) ->
		{
			this.animation.animates = this.animates.isToggled();
		});
		this.animates.flex().h(14);

		this.ignored = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.ignored"), false, (b) ->
		{
			this.animation.ignored = this.ignored.isToggled();
		});
		this.ignored.flex().h(14);

		this.animationDuration = new GuiTrackpadElement(mc, (value) ->
		{
			this.animation.duration = value.intValue();
		});
		this.animationDuration.tooltip(IKey.lang("blockbuster.gui.builder.animation_duration"));
		this.animationDuration.limit(0).integer();

		this.pickInterpolation = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_interpolation"), (b) ->
		{
			this.interpolations.toggleVisible();
		});

		this.interpolations = new GuiInterpolationList(mc, (interp) ->
		{
			this.animation.interp = interp.get(0);
		});
		this.interpolations.flex().relative(this.pickInterpolation).y(1F).w(1F).h(96);

		this.flex().column(5).vertical().stretch().height(20).padding(10);
		this.add(this.animates, this.animationDuration, this.ignored, this.pickInterpolation);
	}

	public void fill(Animation animation)
	{
		this.animation = animation;

		this.animates.toggled(animation.animates);
		this.ignored.toggled(animation.ignored);
		this.animationDuration.setValue(animation.duration);
		this.interpolations.setCurrent(animation.interp);
		this.interpolations.setVisible(false);
	}
}
