package mchorse.blockbuster.client.gui.dashboard.panels.director;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketRequestScenes;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.recording.director.Director;
import mchorse.blockbuster.recording.director.Scene;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Scene manager  
 */
public class GuiSceneManager extends GuiElement
{
	public GuiElements<IGuiElement> blocks = new GuiElements<IGuiElement>();
	public GuiElements<IGuiElement> scenes = new GuiElements<IGuiElement>();
	public GuiDirectorPanel parent;
	public GuiDirectorBlockList directors;
	public GuiStringListElement sceneList;

	public GuiButtonElement<GuiTextureButton> add;
	public GuiButtonElement<GuiTextureButton> dupe;
	public GuiButtonElement<GuiTextureButton> rename;
	public GuiButtonElement<GuiTextureButton> remove;
	public GuiDelegateElement<IGuiElement> modal;

	public Area toggle = new Area();

	public GuiSceneManager(Minecraft mc, GuiDirectorPanel parent)
	{
		super(mc);

		this.parent = parent;
		this.directors = new GuiDirectorBlockList(mc, (director) -> this.parent.pickDirector(director.getPos()));
		this.directors.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);

		this.sceneList = new GuiStringListElement(mc, (scene) -> Dispatcher.sendToServer(new PacketSceneRequestCast(scene)));
		this.modal = new GuiDelegateElement<IGuiElement>(mc, null);
		this.add = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 32, 32, 32, 48, (b) ->
		{
			this.modal.setDelegate(new GuiPromptModal(mc, this.modal, "Enter a name for a new scene:", (name) ->
			{
				if (!this.sceneList.getList().contains(name))
				{
					/* TODO: Check for illegal characters */
					Scene scene = new Scene();

					scene.id = name;
					this.sceneList.add(name);
					this.sceneList.sort();
					this.sceneList.setCurrent(name);

					this.parent.setScene(scene, null);
				}
			}));
		});
		this.dupe = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 48, 32, 48, 48, (b) ->
		{
			if (this.sceneList.getCurrent() == null)
			{
				return;
			}

			this.modal.setDelegate(new GuiPromptModal(mc, this.modal, "Enter a name for duplicated scene:", (name) ->
			{
				if (!this.sceneList.getList().contains(name))
				{
					/* TODO: Check for illegal characters */
					Scene scene = new Scene();

					scene.copy(this.parent.getScene());
					scene.id = name;
					this.sceneList.add(name);
					this.sceneList.sort();
					this.sceneList.setCurrent(name);

					this.parent.setScene(scene, null);
					this.parent.close();
				}
			}));
		});
		this.rename = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 64, 96, 64, 112, (b) ->
		{
			if (this.sceneList.getCurrent() == null)
			{
				return;
			}

			this.modal.setDelegate(new GuiPromptModal(mc, this.modal, "Enter a new name for current scene:", (name) ->
			{
				if (!this.sceneList.getList().contains(name))
				{
					/* TODO: Check for illegal characters */
					String old = this.parent.getScene().id;

					this.sceneList.remove(old);
					this.parent.getScene().id = name;
					this.sceneList.add(name);
					this.sceneList.sort();
					this.sceneList.setCurrent(name);

					Dispatcher.sendToServer(new PacketSceneManage(old, name, PacketSceneManage.REMOVE));
				}
			}));
		});
		this.remove = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 64, 32, 64, 48, (b) ->
		{
			if (this.sceneList.getCurrent() == null)
			{
				return;
			}

			this.modal.setDelegate(new GuiConfirmModal(mc, this.modal, "Are you sure you want to remove this scene?", (value) ->
			{
				if (value)
				{
					String name = this.parent.getScene().id;

					this.sceneList.remove(name);
					this.sceneList.update();
					this.sceneList.setCurrent(null);
					this.parent.setScene(null, null);

					Dispatcher.sendToServer(new PacketSceneManage(name, "", PacketSceneManage.REMOVE));
				}
			}));
		});

		this.sceneList.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);
		this.modal.resizer().parent(this.area).w(1, 0).h(1, 0);
		this.add.resizer().parent(this.area).set(0, 2, 16, 16).x(1, -78);
		this.dupe.resizer().relative(this.add.resizer()).set(20, 0, 16, 16);
		this.rename.resizer().relative(this.dupe.resizer()).set(20, 0, 16, 16);
		this.remove.resizer().relative(this.rename.resizer()).set(20, 0, 16, 16);

		/* Add children */
		this.blocks.add(this.directors);
		this.scenes.add(this.sceneList, this.add, this.dupe, this.rename, this.remove, this.modal);

		this.blocks.setVisible(false);
		this.scenes.setVisible(false);

		this.createChildren();
		this.children.add(this.blocks, this.scenes);
	}

	public void setScene(Scene scene)
	{
		boolean isScene = !(scene instanceof Director);

		this.blocks.setVisible(!isScene);
		this.scenes.setVisible(isScene);
	}

	public void updateList(List<BlockPos> blocks)
	{
		this.directors.clear();

		for (BlockPos pos : blocks)
		{
			this.directors.addBlock(pos);
		}
	}

	public void updateSceneList()
	{
		Dispatcher.sendToServer(new PacketRequestScenes());
	}

	public void add(List<String> scenes)
	{
		String current = this.sceneList.getCurrent();

		if (this.parent.isScene())
		{
			current = this.parent.getScene().id;
		}

		this.sceneList.clear();
		this.sceneList.add(scenes);
		this.sceneList.sort();
		this.sceneList.setCurrent(current);
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