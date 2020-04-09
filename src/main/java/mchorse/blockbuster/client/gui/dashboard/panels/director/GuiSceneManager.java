package mchorse.blockbuster.client.gui.dashboard.panels.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketRequestScenes;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.recording.scene.Director;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.blockbuster.recording.scene.SceneManager;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
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
	public GuiElement blocks;
	public GuiElement scenes;
	public GuiDirectorPanel parent;
	public GuiDirectorBlockList directors;
	public GuiStringListElement sceneList;

	/* Elements for scene manager */
	public GuiIconElement add;
	public GuiIconElement dupe;
	public GuiIconElement rename;
	public GuiIconElement remove;

	/* Elements for director block manager */
	public GuiIconElement convert;
	public Area toggle = new Area();

	public GuiSceneManager(Minecraft mc, GuiDirectorPanel parent)
	{
		super(mc);

		this.parent = parent;
		this.blocks = new GuiElement(mc);
		this.scenes = new GuiElement(mc);

		/* Director block manager list */
		this.directors = new GuiDirectorBlockList(mc, (director) -> this.parent.pickDirector(director.get(0).getPos()));
		this.convert = new GuiIconElement(mc, Icons.REFRESH, (b) -> this.convertScene());
		this.convert.tooltip(I18n.format("blockbuster.gui.director.convert"));

		this.directors.flex().relative(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);
		this.convert.flex().relative(this.area).set(0, 2, 16, 16).x(1, -18);

		this.blocks.add(this.directors, this.convert);

		/* Scene manager elements */
		this.sceneList = new GuiStringListElement(mc, (scene) -> this.switchScene(scene.get(0)));
		this.add = new GuiIconElement(mc, Icons.ADD, (b) -> this.addScene());
		this.dupe = new GuiIconElement(mc, Icons.DUPE, (b) -> this.dupeScene());
		this.rename = new GuiIconElement(mc, Icons.EDIT, (b) -> this.renameScene());
		this.remove = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeScene());

		this.sceneList.flex().relative(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);
		this.add.flex().relative(this.area).set(0, 2, 16, 16).x(1, -78);
		this.dupe.flex().relative(this.add.resizer()).set(20, 0, 16, 16);
		this.rename.flex().relative(this.dupe.resizer()).set(20, 0, 16, 16);
		this.remove.flex().relative(this.rename.resizer()).set(20, 0, 16, 16);

		/* Add children */
		this.scenes.add(this.sceneList, this.add, this.dupe, this.rename, this.remove);
		this.add(this.blocks, this.scenes);
	}

	/* Popup callbacks */

	private void convertScene()
	{
		if (!this.parent.getLocation().isDirector())
		{
			return;
		}

		GuiModal.addFullModal(this.blocks, () -> new GuiPromptModal(this.mc, I18n.format("blockbuster.gui.director.convert_modal"), (name) ->
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
		GuiModal.addFullModal(this.scenes, () -> new GuiPromptModal(this.mc, I18n.format("blockbuster.gui.scenes.add_modal"), (name) ->
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
		if (!this.parent.getLocation().isScene())
		{
			return;
		}

		GuiModal.addFullModal(this, () ->
		{
			GuiPromptModal modal = new GuiPromptModal(this.mc, I18n.format("blockbuster.gui.scenes.dupe_modal"), (name) ->
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

			return modal.setValue(this.parent.getLocation().getFilename());
		});
	}

	private void renameScene()
	{
		if (!this.parent.getLocation().isScene())
		{
			return;
		}

		GuiModal.addFullModal(this, () ->
		{
			GuiPromptModal modal = new GuiPromptModal(mc, I18n.format("blockbuster.gui.scenes.rename_modal"), (name) ->
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

			return modal.setValue(this.parent.getLocation().getFilename());
		});
	}

	private void removeScene()
	{
		if (!this.parent.getLocation().isScene())
		{
			return;
		}

		GuiModal.addFullModal(this, () -> new GuiConfirmModal(this.mc, I18n.format("blockbuster.gui.scenes.remove_modal"), (value) ->
		{
			if (!value) return;

			String name = this.parent.getLocation().getFilename();

			this.sceneList.remove(name);
			this.sceneList.update();
			this.sceneList.setCurrent((String) null);
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
		this.sceneList.clear();
		this.sceneList.add(scenes);
		this.sceneList.sort();
	}

	@Override
	public void resize()
	{
		super.resize();

		this.toggle.set(this.area.x, this.area.y, 60, 20);
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.toggle.isInside(context))
		{
			this.scenes.setVisible(!this.scenes.isVisible());
			this.blocks.setVisible(!this.blocks.isVisible());

			return true;
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		this.area.draw(0xaa000000);
		Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 20, 0x88000000);

		int color = this.toggle.isInside(context) ? 16777120 : 0xffffff;

		if (this.blocks.isVisible())
		{
			this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.title"), this.area.x + 6, this.area.y + 7, color);
		}
		else
		{
			this.font.drawStringWithShadow(I18n.format("blockbuster.gui.scenes.title"), this.area.x + 6, this.area.y + 7, color);
		}

		super.draw(context);
	}
}