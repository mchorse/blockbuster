package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiActor extends GuiBase
{
	public GuiMorphRenderer renderer;
	public GuiButtonElement pick;
	public GuiCreativeMorphsMenu morphs;

	private EntityActor actor;

	public GuiActor(Minecraft mc, EntityActor actor)
	{
		this.actor = actor;

		this.renderer = new GuiMorphRenderer(mc);
		this.renderer.morph = actor.morph.get();
		this.renderer.flex().reset().relative(this.viewport).wh(1F, 1F);

		this.pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.pick"), (button) ->
		{
			this.morphs.resize();
			this.morphs.setSelected(this.renderer.morph);
			this.root.add(this.morphs);
		});
		this.pick.flex().relative(this.viewport).x(0.5F).y(1F, -10).w(100).anchor(0.5F, 1F);

		this.morphs = new GuiCreativeMorphsMenu(mc, (morph) -> this.renderer.morph = morph);
		this.morphs.flex().reset().relative(this.viewport).wh(1F, 1F);

		this.root.add(this.renderer, this.pick);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	protected void closeScreen()
	{
		this.actor.morph.setDirect(this.renderer.morph);
		Dispatcher.sendToServer(new PacketModifyActor(this.actor));

		super.closeScreen();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.context.font, I18n.format("blockbuster.gui.actor.title"), this.width / 2, 16, 0xffffff);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}