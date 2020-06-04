package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.resource.StreamEntry;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExporter;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiListModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.resources.RLUtils;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class GuiModelList extends GuiModelEditorTab
{
    public GuiStringSearchListElement models;
    private GuiIconElement dupe;
    private GuiIconElement export;
    private GuiIconElement folder;

    /* Main properties */
    private GuiTextElement name;
    private GuiTwoElement texture;
    private GuiThreeElement scale;
    private GuiTrackpadElement scaleGui;
    private GuiButtonElement defaultTexture;
    private GuiTextElement skins;
    private GuiToggleElement providesObj;
    private GuiToggleElement providesMtl;

    public GuiModelList(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.title = IKey.lang("blockbuster.gui.me.models.title");

        this.models = new GuiStringSearchListElement(mc, (str) -> this.panel.setModel(str.get(0)));
        this.models.flex().relative(this.area).y(20).w(140).h(1, -20);

        this.dupe = new GuiIconElement(mc, Icons.DUPE, (b) -> this.saveModel());
        this.export = new GuiIconElement(mc, Icons.UPLOAD, (b) -> this.exportModel());
        this.folder = new GuiIconElement(mc, Icons.FOLDER, (b) -> this.openFolder());

        /* Main properties */
        this.name = new GuiTextElement(mc, 120, (str) -> this.panel.model.name = str);
        this.texture = new GuiTwoElement(mc, (value) ->
        {
            this.panel.model.texture[0] = value[0].intValue();
            this.panel.model.texture[1] = value[1].intValue();
            this.panel.rebuildModel();
        });
        this.texture.setLimit(1, 8196, true);
        this.scale = new GuiThreeElement(mc, (value) ->
        {
            this.panel.model.scale[0] = value[0].floatValue();
            this.panel.model.scale[1] = value[1].floatValue();
            this.panel.model.scale[2] = value[2].floatValue();
        });
        this.scaleGui = new GuiTrackpadElement(mc, (value) ->
        {
            this.panel.model.scaleGui = value.floatValue();
            this.panel.dirty();
        });
        this.scaleGui.tooltip(IKey.lang("blockbuster.gui.me.options.scale_gui"));
        this.defaultTexture = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.me.options.default_texture"), (b) ->
        {
            this.panel.pickTexture(this.panel.model.defaultTexture, (rl) ->
            {
                this.panel.model.defaultTexture = rl;
                this.panel.dirty();
            });
        });
        this.skins = new GuiTextElement(mc, 120, (str) ->
        {
            this.panel.model.skins = str;
            this.panel.dirty();
        });
        this.providesObj = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.options.provides_obj"), false, (b) ->
        {
            this.panel.model.providesObj = b.isToggled();
            this.panel.rebuildModel();
        });
        this.providesMtl = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.options.provides_mtl"), false, (b) ->
        {
            this.panel.model.providesMtl = b.isToggled();
            this.panel.rebuildModel();
        });

        GuiScrollElement element = new GuiScrollElement(mc, ScrollArea.ScrollDirection.HORIZONTAL);

        element.flex().relative(this.models).x(1F).y(-20).hTo(this.area, 1F).wTo(this.area, 1F);
        element.flex().column(5).width(180).scroll().padding(10).height(20);
        element.add(Elements.label(IKey.lang("blockbuster.gui.me.options.name")), this.name);
        element.add(Elements.label(IKey.lang("blockbuster.gui.me.options.texture")), this.texture);
        element.add(Elements.label(IKey.lang("blockbuster.gui.me.options.scale")), this.scale, this.scaleGui, this.defaultTexture);
        element.add(Elements.label(IKey.lang("blockbuster.gui.me.options.skins")), this.skins, this.providesObj, this.providesMtl);

        GuiElement sidebar = Elements.row(mc, 0, 0, 20, this.dupe, this.export, this.folder);

        sidebar.flex().relative(this.models).x(1F).y(-20).h(20).anchorX(1F).row(0).resize();

        this.add(this.models, element, sidebar);
    }

    public void updateModelList()
    {
        String current = this.models.list.getCurrentFirst();

        this.models.list.clear();
        this.models.list.add(ModelCustom.MODELS.keySet());
        this.models.list.sort();

        if (current == null)
        {
            current = "steve";
        }

        if (current != null)
        {
            this.models.list.setCurrentScroll(current);
        }
    }

    public void fillData(Model model)
    {
        this.name.setText(model.name);
        this.texture.setValues(model.texture[0], model.texture[1]);
        this.scale.setValues(model.scale[0], model.scale[1], model.scale[2]);
        this.scaleGui.setValue(model.scaleGui);
        this.skins.setText(model.skins);
        this.providesObj.toggled(model.providesObj);
        this.providesMtl.toggled(model.providesMtl);
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
        boolean exists = ModelCustom.MODELS.containsKey(name);

        if (!exists)
        {
            if (!this.panel.saveModel(name))
            {
                return;
            }

            this.models.list.add(name);
            this.models.list.sort();
            this.models.list.setCurrent(name);
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

    private void openFolder()
    {
        GuiUtils.openWebLink(new File(ClientProxy.configFile, "models/" + this.panel.modelName).toURI());
    }

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0xaa000000);

        super.draw(context);
    }
}