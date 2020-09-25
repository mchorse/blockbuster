package mchorse.blockbuster.client.gui.dashboard;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiTextureManagerPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.director.GuiDirectorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_block.GuiModelBlockPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.events.MultiskinProcessedEvent;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.events.RemoveDashboardPanels;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;
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
    public GuiDirectorPanel directorPanel;
    public GuiModelBlockPanel modelPanel;
    public GuiModelEditorPanel modelEditorPanel;
    public GuiRecordingEditorPanel recordingEditorPanel;
    public GuiTextureManagerPanel texturePanel;
    public GuiSnowstorm particleEditor;

    public GuiCreativeMorphsMenu morphs;

    public void picker(Consumer<AbstractMorph> callback)
    {
        this.morphs.removeFromParent();
        this.morphs.callback = callback;
    }

    public void addMorphs(GuiElement parent, boolean editing, AbstractMorph morph)
    {
        if (this.morphs.hasParent())
        {
            return;
        }

        this.morphs.flex().reset().relative(parent).wh(1F, 1F);
        this.morphs.resize();
        this.morphs.setSelected(morph);

        if (editing)
        {
            this.morphs.enterEditMorph();
        }

        parent.add(this.morphs);
    }

    @SubscribeEvent
    public void onRegister(RegisterDashboardPanels event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.directorPanel = new GuiDirectorPanel(mc, event.dashboard);
        this.modelPanel = new GuiModelBlockPanel(mc, event.dashboard);
        this.modelEditorPanel = new GuiModelEditorPanel(mc, event.dashboard);
        this.recordingEditorPanel = new GuiRecordingEditorPanel(mc, event.dashboard);
        this.texturePanel = new GuiTextureManagerPanel(mc, event.dashboard);
        this.particleEditor = new GuiSnowstorm(mc, event.dashboard);

        this.morphs = new GuiCreativeMorphsMenu(mc, null);

        event.dashboard.panels.registerPanel(this.directorPanel, IKey.lang("blockbuster.gui.dashboard.director"), BBIcons.SCENE);
        event.dashboard.panels.registerPanel(this.modelPanel, IKey.lang("blockbuster.gui.dashboard.model"), MMIcons.BLOCK);
        event.dashboard.panels.registerPanel(this.modelEditorPanel, IKey.lang("blockbuster.gui.dashboard.model_editor"), Icons.POSE);
        event.dashboard.panels.registerPanel(this.recordingEditorPanel, IKey.lang("blockbuster.gui.dashboard.player_recording"), BBIcons.EDITOR);
        event.dashboard.panels.registerPanel(this.texturePanel, IKey.lang("blockbuster.gui.dashboard.texture"), Icons.MATERIAL);
        event.dashboard.panels.registerPanel(this.particleEditor, IKey.lang("blockbuster.gui.dashboard.particle"), BBIcons.PARTICLE);
    }

    @SubscribeEvent
    public void onUnregister(RemoveDashboardPanels event)
    {
        GuiModelBlockPanel.lastBlocks.clear();
        GuiDirectorPanel.lastBlocks.clear();
        ClientProxy.audio.reset();

        this.directorPanel = null;
        this.modelPanel = null;
        this.recordingEditorPanel = null;

        this.morphs = null;
    }

    @SubscribeEvent
    public void onMultiskinLoad(MultiskinProcessedEvent event)
    {
        ModelExtrudedLayer.forceReload(event.location, event.image);
    }
}