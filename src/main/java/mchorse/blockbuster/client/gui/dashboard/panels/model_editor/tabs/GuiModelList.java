package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.resource.StreamEntry;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiListModal;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExporter;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class GuiModelModels extends GuiModelEditorTab
{
    public GuiStringListElement modelList;
    private GuiIconElement save;
    public GuiIconElement export;

    public GuiModelModels(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.title = IKey.lang("blockbuster.gui.me.models.title");

        this.modelList = new GuiStringListElement(mc, (str) -> this.panel.setModel(str.get(0)));
        this.modelList.flex().set(0, 20, 80, 0).relative(this.area).h(1, -20).w(1, 0);
        this.add(this.modelList);

        this.save = new GuiIconElement(mc, Icons.SAVED, (b) -> this.saveModel());
        this.save.tooltip(IKey.lang("blockbuster.gui.me.models.save"));
        this.export = new GuiIconElement(mc, Icons.UPLOAD, (b) -> this.exportModel());
        this.export.tooltip(IKey.lang("blockbuster.gui.me.models.export"));

        this.save.flex().set(2, 2, 16, 16).relative(this.area).x(1, -18);
        this.export.flex().set(-20, 0, 16, 16).relative(this.save.resizer());
        this.add(this.save, this.export);
    }

    public void updateModelList()
    {
        this.modelList.clear();
        this.modelList.add(ModelCustom.MODELS.keySet());
        this.modelList.sort();
    }

    private void saveModel()
    {
        GuiModal.addFullModal(this, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(mc, IKey.lang("blockbuster.gui.me.models.name"), this::saveModel);

            return modal.setValue(this.panel.modelName);
        });
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

        for (Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries())
        {
            Class<? extends Entity> clazz = entry.getValue().getEntityClass();

            while (clazz != null)
            {
                if (clazz == EntityLivingBase.class)
                {
                    mobs.add(entry.getKey().toString());
                    break;
                }
                else
                {
                    clazz = (Class<? extends Entity>) clazz.getSuperclass();
                }
            }
        }

        Collections.sort(mobs);

        GuiModal.addFullModal(this, () ->
        {
            GuiListModal modal = new GuiListModal(this.mc, IKey.lang("blockbuster.gui.me.models.pick"), this::exportModel);

            return modal.addValues(mobs);
        });
    }

    private void exportModel(String name)
    {
        if (name.isEmpty())
        {
            return;
        }

        try
        {
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(name), this.mc.world);
            Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
            ModelExporter exporter = new ModelExporter((EntityLivingBase) entity, (RenderLivingBase) render);
            Model model = exporter.exportModel(name);

            name = name.replaceAll(":", "_");
            model.fillInMissing();
            this.panel.setModel(name, model, new ModelLazyLoaderJSON(new StreamEntry("", 0)));
        }
        catch (Exception e)
        {
            GuiModal.addFullModal(this, () -> new GuiMessageModal(this.mc, IKey.str(I18n.format("blockbuster.gui.me.models.error", e.getMessage()))));

            e.printStackTrace();
        }
    }
}