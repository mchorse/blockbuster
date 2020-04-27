package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiModelOptions extends GuiModelEditorTab
{
    private GuiTextElement name;
    private GuiTwoElement texture;
    private GuiThreeElement scale;
    private GuiTrackpadElement scaleGui;
    private GuiTextElement defaultTexture;
    private GuiTextElement skins;
    private GuiToggleElement providesObj;
    private GuiToggleElement providesMtl;

    public GuiModelOptions(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.title = IKey.str("");

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
            this.panel.model.scale[0] = value[0];
            this.panel.model.scale[1] = value[1];
            this.panel.model.scale[2] = value[2];
        });
        this.scaleGui = new GuiTrackpadElement(mc, (value) -> this.panel.model.scaleGui = value);
        this.scaleGui.tooltip(IKey.lang("blockbuster.gui.me.options.scale_gui"));
        this.defaultTexture = new GuiTextElement(mc, 1000, (str) -> this.panel.model.defaultTexture = str.isEmpty() ? null : RLUtils.create(str));
        this.skins = new GuiTextElement(mc, 120, (str) -> this.panel.model.skins = str);
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

        int w = 120;

        this.name.flex().relative(this.area).set(10, 20, w, 20);
        this.texture.flex().set(0, 40, w, 20).relative(this.name.resizer());
        this.scale.flex().set(0, 40, w, 20).relative(this.texture.resizer());
        this.scaleGui.flex().set(0, 25, w, 20).relative(this.scale.resizer());
        this.defaultTexture.flex().set(0, 40, w, 20).relative(this.scaleGui.resizer());
        this.skins.flex().set(0, 40, w, 20).relative(this.defaultTexture.resizer());
        this.providesObj.flex().set(0, 25, w, 11).relative(this.skins.resizer());
        this.providesMtl.flex().set(0, 16, w, 11).relative(this.providesObj.resizer());

        this.add(this.name, this.texture, this.scale, this.scaleGui, this.defaultTexture, this.skins, this.providesObj, this.providesMtl);
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return super.mouseClicked(context) || this.area.isInside(context);
    }

    public void fillData(Model model)
    {
        this.name.setText(model.name);
        this.texture.setValues(model.texture[0], model.texture[1]);
        this.scale.setValues(model.scale[0], model.scale[1], model.scale[2]);
        this.scaleGui.setValue(model.scaleGui);
        this.defaultTexture.setText(model.defaultTexture == null ? "" : model.defaultTexture.toString());
        this.skins.setText(model.skins);
        this.providesObj.toggled(model.providesObj);
        this.providesMtl.toggled(model.providesMtl);
    }

    @Override
    protected void drawLabels()
    {
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.options.name"), this.name.area.x, this.name.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.options.texture"), this.texture.area.x, this.texture.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.options.scale"), this.scale.area.x, this.scale.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.options.default"), this.defaultTexture.area.x, this.defaultTexture.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.options.skins"), this.skins.area.x, this.skins.area.y - 12, 0xeeeeee);
    }
}