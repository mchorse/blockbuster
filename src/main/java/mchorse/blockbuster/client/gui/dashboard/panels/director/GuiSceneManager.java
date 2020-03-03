package mchorse.blockbuster.client.gui.dashboard.panels.director;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketRequestScenes;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.recording.scene.Director;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.blockbuster.recording.scene.SceneManager;
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
 * Scene manager GUI
 */
public class GuiSceneManager extends GuiElement
{
	public GuiElements<IGuiElement> blocks = new GuiElements<IGuiElement>();
	public GuiElements<IGuiElement> scenes = new GuiElements<IGuiElement>();
	public GuiDirectorPanel parent;
	public GuiDirectorBlockList directors;
	public GuiStringListElement sceneList;

	/* Elements for scene manager */
	public GuiButtonElement<GuiTextureButton> add;
	public GuiButtonElement<GuiTextureButton> dupe;
	public GuiButtonElement<GuiTextureButton> rename;
	public GuiButtonElement<GuiTextureButton> remove;
	public GuiDelegateElement<IGuiElement> sceneModal;

	/* Elements for director block manager */
	public GuiButtonElement<GuiTextureButton> convert;
	public GuiDelegateElement<IGuiElement> directorModal;
	public Area toggle = new Area();

	public GuiSceneManager(Minecraft mc, GuiDirectorPanel parent)
	{
		super(mc);

		this.parent = parent;

		/* Director block manager list */
		this.directors = new GuiDirectorBlockList(mc, (director) -> this.parent.pickDirector(director.getPos()));
		this.directorModal = new GuiDelegateElement<IGuiElement>(mc, null);
		this.convert = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 64, 64, 64, 80, (b) -> this.convertScene()).tooltip(I18n.format("blockbuster.gui.director.convert"), GuiTooltip.TooltipDirection.BOTTOM);

		this.directors.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);
		this.directorModal.resizer().parent(this.area).w(1, 0).h(1, 0);
		this.convert.resizer().parent(this.area).set(0, 2, 16, 16).x(1, -18);

		this.blocks.add(this.directors, this.convert, this.directorModal);

		/* Scene manager elements */
		this.sceneList = new GuiStringListElement(mc, (scene) -> this.switchScene(scene));
		this.sceneModal = new GuiDelegateElement<IGuiElement>(mc, null);
		this.add = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 32, 32, 32, 48, (b) -> this.addScene());
		this.dupe = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 48, 32, 48, 48, (b) -> this.dupeScene());
		this.rename = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 64, 96, 64, 112, (b) -> this.renameScene());
		this.remove = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 64, 32, 64, 48, (b) -> this.removeScene());

		this.sceneList.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);
		this.sceneModal.resizer().parent(this.area).w(1, 0).h(1, 0);
		this.add.resizer().parent(this.area).set(0, 2, 16, 16).x(1, -78);
		this.dupe.resizer().relative(this.add.resizer()).set(20, 0, 16, 16);
		this.rename.resizer().relative(this.dupe.resizer()).set(20, 0, 16, 16);
		this.remove.resizer().relative(this.rename.resizer()).set(20, 0, 16, 16);

		/* Add children */
		this.scenes.add(this.sceneList, this.add, this.dupe, this.rename, this.remove, this.sceneModal);

		this.createChildren();
		this.children.add(this.blocks, this.scenes);
	}

	/* Popup callbacks */

	private void convertScene()
	{
		if (!this.parent.getLocation().isDirector()) return;

		this.parent.unfocus();
		this.directorModal.setDelegate(new GuiPromptModal(mc, this.directorModal, I18n.format("blockbuster.gui.director.convert_modal"), (name) ->
		{
			if (this.sceneList.getList().contains(name) || !SceneManager.isValidFilename(name)) return;

			Scene scene = new Scene();

			scene.setId(name);
			scene.copy(this.parent.getLocation().getDirector());
			scene.setupIds();
			this.sceneList.add(name);
			this.sceneList.sort();
			this.sceneList.setCurrent(name);

			this.parent.setScene(new SceneLocation(scene));
		}));
	}

	private void switchScene(String scene)
	{
		this.parent.close();
		Dispatcher.sendToServer(new PacketSceneRequestCast(new SceneLocation(scene)));
	}

	private void addScene()
	{
		this.parent.unfocus();
		this.sceneModal.setDelegate(new GuiPromptModal(mc, this.sceneModal, I18n.format("blockbuster.gui.scenes.add_modal"), (name) ->
		{
			if (this.sceneList.getList().contains(name) || !SceneManager.isValidFilename(name)) return;

			Scene scene = new Scene();

			scene.setId(name);
			this.sceneList.add(name);
			this.sceneList.sort();
			this.sceneList.setCurrent(name);

			this.parent.setScene(new SceneLocation(scene));
		}));
	}

	private void dupeScene()
	{
		if (!this.parent.getLocation().isScene()) return;

		this.parent.unfocus();

		GuiPromptModal modal = new GuiPromptModal(mc, this.sceneModal, I18n.format("blockbuster.gui.scenes.dupe_modal"), (name) ->
		{
			if (this.sceneList.getList().contains(name) || !SceneManager.isValidFilename(name)) return;

			Scene scene = new Scene();

			scene.copy(this.parent.getLocation().getScene());
			scene.setId(name);
			scene.setupIds();
			this.sceneList.add(name);
			this.sceneList.sort();
			this.sceneList.setCurrent(name);

			this.parent.setScene(new SceneLocation(scene));
			this.parent.close();
		});

		modal.setValue(this.parent.getLocation().getFilename());
		this.sceneModal.setDelegate(modal);
	}

	private void renameScene()
	{
		if (!this.parent.getLocation().isScene()) return;

		this.parent.unfocus();

		GuiPromptModal modal = new GuiPromptModal(mc, this.sceneModal, I18n.format("blockbuster.gui.scenes.rename_modal"), (name) ->
		{
			if (this.sceneList.getList().contains(name) || !SceneManager.isValidFilename(name)) return;

			String old = this.parent.getLocation().getFilename();

			this.sceneList.remove(old);
			this.parent.getLocation().getScene().setId(name);
			this.sceneList.add(name);
			this.sceneList.sort();
			this.sceneList.setCurrent(name);
			this.parent.setScene(new SceneLocation(this.parent.getLocation().getScene()));

			Dispatcher.sendToServer(new PacketSceneManage(old, name, PacketSceneManage.RENAME));
		});

		modal.setValue(this.parent.getLocation().getFilename());
		this.sceneModal.setDelegate(modal);
	}

	private void removeScene()
	{
		if (!this.parent.getLocation().isScene()) return;

		this.parent.unfocus();

		this.sceneModal.setDelegate(new GuiConfirmModal(mc, this.sceneModal, I18n.format("blockbuster.gui.scenes.remove_modal"), (value) ->
		{
			if (!value) return;

			String name = this.parent.getLocation().getFilename();

			this.sceneList.remove(name);
			this.sceneList.update();
			this.sceneList.setCurrent(null);
			this.parent.setScene(new SceneLocation());

			Dispatcher.sendToServer(new PacketSceneManage(name, "", PacketSceneManage.REMOVE));
		}));
	}

	/* Scene manager methods */

	public void setScene(Scene scene)
	{
		boolean isDirector = scene instanceof Director;

		this.blocks.setVisible(isDirector);
		this.scenes.setVisible(!isDirector);

		if (scene instanceof Director)
		{
			this.sceneList.setCurrent("");
		}
		else if (scene instanceof Scene)
		{
			this.sceneList.setCurrent(scene.getId());
		}
	}

	public void updateList(List<BlockPos> blocks)
	{
		this.directors.clear();

		for (BlockPos pos : blocks)
		{
			this.directors.addBlock(pos);
		}

		if (this.parent.getLocation().isDirector())
		{
			for (TileEntityDirector tile : this.directors.getList())
			{
				BlockPos pos = tile.getPos();

				if (pos.equals(this.parent.getLocation().getPosition()))
				{
					this.directors.setCurrent(tile);

					break;
				}
			}
		}
	}

	public void updateSceneList()
	{
		Dispatcher.sendToServer(new PacketRequestScenes());
	}

	public void add(List<String> scenes)
	{
		String current = this.sceneList.getCurrent();

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
			this.font.drawStringWithShadow(I18n.format("blockbuster.gui.scenes.title"), this.area.x + 6, this.area.y + 7, color);
		}

		super.draw(tooltip, mouseX, mouseY, partialTicks);
	}
}