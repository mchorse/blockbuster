package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiListModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiMessageModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiPromptModal;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExporter;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.Resizer.Measure;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;

public class GuiModelModels extends GuiModelEditorTab
{
    public GuiStringListElement modelList;
    private GuiButtonElement<GuiTextureButton> save;
    private GuiButtonElement<GuiTextureButton> export;
    private GuiDelegateElement<IGuiElement> modal;

    public GuiModelModels(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.title = I18n.format("blockbuster.gui.me.models.title");

        this.modelList = new GuiStringListElement(mc, (str) -> this.panel.setModel(str));
        this.modelList.resizer().set(0, 20, 80, 0).parent(this.area).h(1, -20).w(1, 0);
        this.children.add(this.modelList);

        this.save = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 112, 32, 112, 48, (b) -> this.saveModel()).tooltip(I18n.format("blockbuster.gui.me.models.save"), TooltipDirection.BOTTOM);
        this.export = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 64, 64, 80, (b) -> this.exportModel()).tooltip(I18n.format("blockbuster.gui.me.models.export"), TooltipDirection.BOTTOM);

        this.save.resizer().set(2, 2, 16, 16).parent(this.area).x(1, -38);
        this.export.resizer().set(20, 0, 16, 16).relative(this.save.resizer());
        this.children.add(this.save, this.export);

        this.modal = new GuiDelegateElement<IGuiElement>(mc, null);
        this.modal.resizer().set(0, 0, 1, 1, Measure.RELATIVE).parent(this.area);
        this.children.add(this.modal);
    }

    public void updateModelList()
    {
        this.modelList.clear();
        this.modelList.add(ModelCustom.MODELS.keySet());
        this.modelList.sort();
    }

    private void saveModel()
    {
        this.modal.setDelegate(new GuiPromptModal(mc, this.modal, I18n.format("blockbuster.gui.me.models.name"), (name) -> this.saveModel(name)).setValue(this.panel.modelName));
    }

    private void saveModel(String name)
    {
        boolean exists = ModelCustom.MODELS.containsKey(this.panel.modelName);
        boolean save = this.panel.saveModel(name);

        if (!exists && save)
        {
            this.modelList.add(name);
            this.modelList.sort();
            this.modelList.setCurrent(name);
        }
    }

    private void exportModel()
    {
        List<String> mobs = new ArrayList<String>();

        for (Map.Entry<String, Class<? extends Entity>> entry : EntityList.NAME_TO_CLASS.entrySet())
        {
            Class<? extends Entity> clazz = entry.getValue();

            while (clazz != null)
            {
                if (clazz == EntityLivingBase.class)
                {
                    mobs.add(entry.getKey());
                    break;
                }
                else
                {
                    clazz = (Class<? extends Entity>) clazz.getSuperclass();
                }
            }
        }

        Collections.sort(mobs);

        this.modal.setDelegate(new GuiListModal(this.mc, this.modal, I18n.format("blockbuster.gui.me.models.pick"), (name) -> this.exportModel(name)).addValues(mobs));
    }

    private void exportModel(String name)
    {
        if (name.isEmpty())
        {
            return;
        }

        try
        {
            Entity entity = EntityList.createEntityByName(name, this.mc.theWorld);
            Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
            ModelExporter exporter = new ModelExporter((EntityLivingBase) entity, (RenderLivingBase) render);
            Model model = exporter.exportModel(name);

            model.fillInMissing();
            this.panel.setModel(name, model);
        }
        catch (Exception e)
        {
            this.modal.setDelegate(new GuiMessageModal(this.mc, this.modal, I18n.format("blockbuster.gui.me.models.error", e.getMessage())));
            e.printStackTrace();
        }
    }
}