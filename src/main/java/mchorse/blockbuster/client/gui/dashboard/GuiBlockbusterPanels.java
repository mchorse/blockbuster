package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.GuiImmersiveEditor;
import mchorse.blockbuster.client.gui.GuiImmersiveMorphMenu;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiTextureManagerPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.scene.GuiScenePanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_block.GuiModelBlockPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.events.MultiskinProcessedEvent;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.events.RemoveDashboardPanels;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

/**
 * Blockbuster's dashboard GUI entry
 */
@SideOnly(Side.CLIENT)
public class GuiBlockbusterPanels
{
    public GuiScenePanel scenePanel;
    public GuiModelBlockPanel modelPanel;
    public GuiModelEditorPanel modelEditorPanel;
    public GuiRecordingEditorPanel recordingEditorPanel;
    public GuiTextureManagerPanel texturePanel;
    public GuiSnowstorm particleEditor;

    public GuiCreativeMorphsMenu morphs;
    public GuiImmersiveEditor immersiveEditor;

    public void picker(Consumer<AbstractMorph> callback)
    {
        this.morphs.removeFromParent();
        this.morphs.callback = callback;
        this.immersiveEditor.morphs.callback = callback;
    }

    public void addMorphs(GuiElement parent, boolean editing, AbstractMorph morph)
    {
        if (this.morphs.hasParent())
        {
            return;
        }

        this.morphs.reload();
        this.morphs.flex().reset().relative(parent).wh(1F, 1F);
        this.morphs.resize();
        this.morphs.setSelected(morph);

        if (editing)
        {
            this.morphs.enterEditMorph();
        }

        parent.add(this.morphs);
    }

    public GuiImmersiveEditor showImmersiveEditor(boolean editing, AbstractMorph morph)
    {
        this.immersiveEditor.show();
        this.immersiveEditor.morphs.setSelected(morph);
        this.immersiveEditor.morphs.updateCallback = null;
        this.immersiveEditor.morphs.target = null;
        this.immersiveEditor.morphs.frameProvider = null;
        this.immersiveEditor.morphs.beforeRender = null;
        this.immersiveEditor.morphs.afterRender = null;

        if (editing)
        {
            this.immersiveEditor.morphs.enterEditMorph();
        }

        return this.immersiveEditor;
    }

    public void closeImmersiveEditor()
    {
        this.immersiveEditor.closeThisScreen();
    }

    @SubscribeEvent
    public void onRegister(RegisterDashboardPanels event)
    {
        if (!(event.dashboard instanceof GuiDashboard))
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        GuiDashboard dashboard = (GuiDashboard) event.dashboard;

        this.scenePanel = new GuiScenePanel(mc, dashboard);
        this.modelPanel = new GuiModelBlockPanel(mc, dashboard);
        this.modelEditorPanel = new GuiModelEditorPanel(mc, dashboard);
        this.recordingEditorPanel = new GuiRecordingEditorPanel(mc, dashboard);
        this.texturePanel = new GuiTextureManagerPanel(mc, dashboard);
        this.particleEditor = new GuiSnowstorm(mc, dashboard);

        this.morphs = new GuiCreativeMorphsMenu(mc, null);
        this.immersiveEditor = new GuiImmersiveEditor(mc);

        dashboard.panels.registerPanel(this.scenePanel, IKey.lang("blockbuster.gui.dashboard.director"), BBIcons.SCENE);
        dashboard.panels.registerPanel(this.modelPanel, IKey.lang("blockbuster.gui.dashboard.model"), Icons.BLOCK);
        dashboard.panels.registerPanel(this.modelEditorPanel, IKey.lang("blockbuster.gui.dashboard.model_editor"), Icons.POSE);
        dashboard.panels.registerPanel(this.recordingEditorPanel, IKey.lang("blockbuster.gui.dashboard.player_recording"), BBIcons.EDITOR);
        dashboard.panels.registerPanel(this.texturePanel, IKey.lang("blockbuster.gui.dashboard.texture"), Icons.MATERIAL);
        dashboard.panels.registerPanel(this.particleEditor, IKey.lang("blockbuster.gui.dashboard.particle"), BBIcons.PARTICLE);
    }

    @SubscribeEvent
    public void onUnregister(RemoveDashboardPanels event)
    {
        GuiModelBlockPanel.lastBlocks.clear();
        ClientProxy.audio.reset();

        this.scenePanel = null;
        this.modelPanel = null;
        this.recordingEditorPanel = null;

        this.morphs = null;
        this.immersiveEditor = null;
    }

    @SubscribeEvent
    public void onMultiskinLoad(MultiskinProcessedEvent event)
    {
        ModelExtrudedLayer.forceReload(event.location, event.image);
    }
}