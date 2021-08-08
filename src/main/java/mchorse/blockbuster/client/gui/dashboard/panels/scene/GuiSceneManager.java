package mchorse.blockbuster.client.gui.dashboard.panels.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketRequestScenes;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.blockbuster.recording.scene.SceneManager;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.List;

/**
 * Scene manager GUI
 */
public class GuiSceneManager extends GuiElement
{
    public GuiScenePanel parent;
    public GuiStringListElement sceneList;

    /* Elements for scene manager */
    public GuiIconElement add;
    public GuiIconElement dupe;
    public GuiIconElement rename;
    public GuiIconElement remove;

    public GuiSceneManager(Minecraft mc, GuiScenePanel parent)
    {
        super(mc);

        this.parent = parent;

        /* Scene manager elements */
        this.sceneList = new GuiStringListElement(mc, (scene) -> this.switchScene(scene.get(0)));
        this.add = new GuiIconElement(mc, Icons.ADD, (b) -> this.addScene());
        this.add.tooltip(IKey.lang("blockbuster.gui.scenes.add_scene"));
        this.dupe = new GuiIconElement(mc, Icons.DUPE, (b) -> this.dupeScene());
        this.dupe.tooltip(IKey.lang("blockbuster.gui.scenes.dupe_scene"));
        this.rename = new GuiIconElement(mc, Icons.EDIT, (b) -> this.renameScene());
        this.rename.tooltip(IKey.lang("blockbuster.gui.scenes.rename_scene"));
        this.remove = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeScene());
        this.remove.tooltip(IKey.lang("blockbuster.gui.scenes.remove_scene"));

        this.sceneList.flex().relative(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);
        this.add.flex().relative(this.area).set(0, 2, 16, 16).x(1, -78);
        this.dupe.flex().relative(this.add.resizer()).set(20, 0, 16, 16);
        this.rename.flex().relative(this.dupe.resizer()).set(20, 0, 16, 16);
        this.remove.flex().relative(this.rename.resizer()).set(20, 0, 16, 16);

        /* Add children */
        this.add(this.sceneList, this.add, this.dupe, this.rename, this.remove);
        this.hideTooltip();
    }

    /* Popup callbacks */

    private void switchScene(String scene)
    {
        this.parent.close();
        Dispatcher.sendToServer(new PacketSceneRequestCast(new SceneLocation(scene)));
    }

    private void addScene()
    {
        GuiModal.addFullModal(this, () -> new GuiPromptModal(this.mc, IKey.lang("blockbuster.gui.scenes.add_modal"), (name) ->
        {
            if (this.sceneList.getList().contains(name) || !SceneManager.isValidFilename(name)) return;

            Scene scene = new Scene();

            scene.setId(name);
            this.sceneList.add(name);
            this.sceneList.sort();
            this.sceneList.setCurrent(name);

            this.parent.setScene(new SceneLocation(scene));
        }).filename());
    }

    private void dupeScene()
    {
        if (!this.parent.getLocation().isScene())
        {
            return;
        }

        GuiModal.addFullModal(this, () ->
        {

            GuiToggleElement dupeRecordings = new GuiToggleElement(this.mc, IKey.lang("blockbuster.gui.scenes.dupe_recordings"), null);

            GuiPromptModal modal = new GuiPromptModal(this.mc, IKey.lang("blockbuster.gui.scenes.dupe_modal"), (name) ->
            {
                if (this.sceneList.getList().contains(name) || !SceneManager.isValidFilename(name)) return;

                Scene scene = new Scene();

                scene.copy(this.parent.getLocation().getScene());
                scene.setId(name);
                scene.setupIds();
                scene.renamePrefix(this.parent.getLocation().getScene().getId(), scene.getId(), (id) -> id + "_copy");

                //copy recordings
                if (dupeRecordings.isToggled())
                {
                    Dispatcher.sendToServer(new PacketSceneManage(this.parent.getLocation().getScene().getId(), name, PacketSceneManage.DUPE));
                }

                this.sceneList.add(name);
                this.sceneList.sort();
                this.sceneList.setCurrent(name);

                this.parent.setScene(new SceneLocation(scene));
                this.parent.close();
            });

            dupeRecordings.tooltip(IKey.lang("blockbuster.gui.scenes.dupe_recordings_tooltip"));
            dupeRecordings.flex().relative(modal.bar).y(-50).x(10).w(1F, -20);
            modal.add(dupeRecordings);

            return modal.filename().setValue(this.parent.getLocation().getFilename());
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
            GuiPromptModal modal = new GuiPromptModal(mc, IKey.lang("blockbuster.gui.scenes.rename_modal"), (name) ->
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

            return modal.filename().setValue(this.parent.getLocation().getFilename());
        });
    }

    private void removeScene()
    {
        if (!this.parent.getLocation().isScene())
        {
            return;
        }

        GuiModal.addFullModal(this, () -> new GuiConfirmModal(this.mc, IKey.lang("blockbuster.gui.scenes.remove_modal"), (value) ->
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
        this.sceneList.setCurrent(scene == null ? "" : scene.getId());
    }

    public void updateSceneList()
    {
        Dispatcher.sendToServer(new PacketRequestScenes());
    }

    public void add(List<String> scenes)
    {
        String current = this.sceneList.getCurrentFirst();

        this.sceneList.clear();
        this.sceneList.add(scenes);
        this.sceneList.sort();
        this.sceneList.setCurrent(current);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0xaa000000);
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 20, ColorUtils.HALF_BLACK);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.scenes.title"), this.area.x + 6, this.area.y + 7, 0xffffff);

        super.draw(context);
    }
}