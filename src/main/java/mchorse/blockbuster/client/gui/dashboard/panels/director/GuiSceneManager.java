package mchorse.blockbuster.client.gui.dashboard.panels.director;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketRequestScenes;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class GuiSceneManager extends GuiElement
{
	public GuiElements<IGuiElement> blocks = new GuiElements<IGuiElement>();
	public GuiElements<IGuiElement> scenes = new GuiElements<IGuiElement>();
	public GuiDirectorPanel parent;
	public GuiDirectorBlockList directors;
	public GuiStringListElement sceneList;

	public Area toggle = new Area();

	public GuiSceneManager(Minecraft mc, GuiDirectorPanel parent)
	{
		super(mc);

		this.parent = parent;
		this.directors = new GuiDirectorBlockList(mc, (director) -> this.parent.pickDirector(director.getPos()));
		this.directors.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);

		this.sceneList = new GuiStringListElement(mc, (scene) -> Dispatcher.sendToServer(new PacketSceneRequestCast(scene)));
		this.sceneList.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);

		this.blocks.add(this.directors);
		this.scenes.add(this.sceneList);

		this.blocks.setVisible(false);
		this.scenes.setVisible(false);

		this.createChildren();
		this.children.add(this.blocks, this.scenes);
	}

	public void setScene(boolean isDirector)
	{
		this.blocks.setVisible(isDirector);
		this.scenes.setVisible(!isDirector);
	}

	public void updateList(List<BlockPos> blocks)
	{
		this.directors.clear();

		for (BlockPos pos : blocks)
		{
			this.directors.addBlock(pos);
		}

		Dispatcher.sendToServer(new PacketRequestScenes());
	}

	public void add(List<String> scenes)
	{
		this.sceneList.clear();
		this.sceneList.add(scenes);
		this.sceneList.sort();
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);

		this.toggle.set(this.area.x, this.area.y, 60, 20);
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		if (super.mouseClicked(mouseX, mouseY, mouseButton))
		{
			return true;
		}

		if (this.toggle.isInside(mouseX, mouseY))
		{
			this.scenes.setVisible(!this.scenes.isVisible());
			this.blocks.setVisible(!this.blocks.isVisible());

			return true;
		}

		return false;
	}

	@Override
	public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
	{
		this.area.draw(0xaa000000);
		Gui.drawRect(this.area.x, this.area.y, this.area.getX(1.0F), this.area.y + 20, 0x88000000);

		int color = this.toggle.isInside(mouseX, mouseY) ? 16777120 : 0xffffff;

		if (this.blocks.isVisible())
		{
			this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.title"), this.area.x + 6, this.area.y + 7, color);
		}
		else
		{
			this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.scenes"), this.area.x + 6, this.area.y + 7, color);
		}

		super.draw(tooltip, mouseX, mouseY, partialTicks);
	}
}