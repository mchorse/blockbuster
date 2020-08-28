package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiModelOptions extends GuiModelEditorTab
{
    /* Main properties */
    private GuiTextElement name;
    private GuiTwoElement texture;
    private GuiTrackpadElement extrudeMaxFactor;
    private GuiTrackpadElement extrudeInwards;
    private GuiThreeElement scale;
    private GuiTrackpadElement scaleGui;
    private GuiButtonElement defaultTexture;
    private GuiTextElement skins;
    private GuiToggleElement providesObj;
    private GuiToggleElement providesMtl;

    public GuiModelOptions(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        /* Main properties */
        this.name = new GuiTextElement(mc, 120, (str) -> this.panel.model.name = str);
        this.texture = new GuiTwoElement(mc, (value) ->
        {
            this.panel.model.texture[0] = value[0].intValue();
            this.panel.model.texture[1] = value[1].intValue();
            this.panel.rebuildModel();
        });
        this.texture.setLimit(1, 8196, true);
        this.extrudeMaxFactor = new GuiTrackpadElement(mc, (value) ->
        {
            this.panel.model.extrudeMaxFactor = value.intValue();
            this.panel.rebuildModel();
        });
        this.extrudeMaxFactor.tooltip(IKey.lang("blockbuster.gui.me.options.extrude_max_factor"));
        this.extrudeMaxFactor.integer().limit(1);
        this.extrudeInwards = new GuiTrackpadElement(mc, (value) ->
        {
            this.panel.model.extrudeInwards = value.intValue();
            this.panel.rebuildModel();
        });
        this.extrudeInwards.tooltip(IKey.lang("blockbuster.gui.me.options.extrude_inwards"));
        this.extrudeInwards.integer().limit(1);
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

        this.flex().column(5).vertical().stretch().scroll().padding(10).height(20);
        this.add(Elements.label(IKey.lang("blockbuster.gui.me.options.name")), this.name);
        this.add(Elements.label(IKey.lang("blockbuster.gui.me.options.texture")), this.texture);
        this.add(Elements.label(IKey.lang("blockbuster.gui.me.options.extrusion")), this.extrudeMaxFactor, this.extrudeInwards);
        this.add(Elements.label(IKey.lang("blockbuster.gui.me.options.scale")), this.scale, this.scaleGui, this.defaultTexture);
        this.add(Elements.label(IKey.lang("blockbuster.gui.me.options.skins")), this.skins, this.providesObj, this.providesMtl);
    }

    public void fillData(Model model)
    {
        this.name.setText(model.name);
        this.texture.setValues(model.texture[0], model.texture[1]);
        this.extrudeMaxFactor.setValue(model.extrudeMaxFactor);
        this.extrudeInwards.setValue(model.extrudeInwards);
        this.scale.setValues(model.scale[0], model.scale[1], model.scale[2]);
        this.scaleGui.setValue(model.scaleGui);
        this.skins.setText(model.skins);
        this.providesObj.toggled(model.providesObj);
        this.providesMtl.toggled(model.providesMtl);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0xaa000000);

        super.draw(context);
    }
}