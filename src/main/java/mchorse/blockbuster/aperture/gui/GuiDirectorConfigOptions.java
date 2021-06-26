package mchorse.blockbuster.aperture.gui;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.client.gui.config.GuiAbstractConfigOptions;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.network.common.PacketAudioShift;
import mchorse.blockbuster.client.gui.dashboard.panels.scene.GuiScenePanel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.sync.PacketScenePlay;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDirectorConfigOptions extends GuiAbstractConfigOptions
{
    private static GuiDirectorConfigOptions instance;

    public GuiButtonElement detachScene;
    public GuiToggleElement actions;
    public GuiToggleElement reload;
    public GuiToggleElement stopScene;
    public GuiButtonElement reloadScene;
    public GuiTrackpadElement audioShift;

    public static GuiDirectorConfigOptions getInstance()
    {
        return instance;
    }

    public GuiDirectorConfigOptions(Minecraft mc, GuiCameraEditor editor)
    {
        super(mc, editor);

        this.detachScene = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.aperture.config.detach"), (b) ->
        {
            if (CameraHandler.location != null)
            {
                Dispatcher.sendToServer(new PacketScenePlay(CameraHandler.location, PacketScenePlay.STOP, 0));

                CameraHandler.location = null;
                b.setEnabled(false);
            }
        });

        this.reload = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.aperture.config.reload"), CameraHandler.reload.get(), (b) ->
        {
            CameraHandler.reload.set(this.reload.isToggled());
        });

        this.actions = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.aperture.config.actions"), CameraHandler.actions.get(), (b) ->
        {
            CameraHandler.actions.set(this.actions.isToggled());
        });
        
        this.stopScene = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.aperture.config.stop_scene"), CameraHandler.stopScene.get(), (b) ->
        {
            CameraHandler.stopScene.set(this.stopScene.isToggled());
        });

        this.reloadScene = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.aperture.config.reload_scene"), (b) ->
        {
            SceneLocation location = CameraHandler.get();

            if (location != null)
            {
                Dispatcher.sendToServer(new PacketScenePlay(location, PacketScenePlay.RESTART, ClientProxy.getCameraEditor().timeline.value));
            }
        });

        this.audioShift = new GuiTrackpadElement(mc, (value) ->
        {
            SceneLocation location = CameraHandler.get();

            if (location != null)
            {
                Dispatcher.sendToServer(new PacketAudioShift(location, value.intValue()));

                GuiScenePanel panel = mchorse.blockbuster.ClientProxy.panels.scenePanel;

                if (panel.getLocation().equals(location))
                {
                    panel.getLocation().getScene().audioShift = value.intValue();
                }
            }
        });
        this.audioShift.limit(0).integer().tooltip(IKey.lang("blockbuster.gui.director.audio_shift_tooltip"));

        this.add(this.detachScene, this.reload, this.actions, this.stopScene, this.reloadScene);
        this.add(Elements.label(IKey.lang("blockbuster.gui.director.audio_shift")).background(), this.audioShift);

        instance = this;
    }

    @Override
    public IKey getTitle()
    {
        return IKey.lang("blockbuster.gui.aperture.config.title");
    }

    @Override
    public void update()
    {
        this.reload.toggled(CameraHandler.reload.get());
        this.actions.toggled(CameraHandler.actions.get());
        this.stopScene.toggled(CameraHandler.stopScene.get());
    }

    @Override
    public void resize()
    {
        super.resize();

        this.detachScene.setEnabled(CameraHandler.location != null);
    }
}