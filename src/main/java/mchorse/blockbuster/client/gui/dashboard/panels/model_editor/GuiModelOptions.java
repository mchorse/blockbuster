package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelOptions extends GuiElement
{
    public GuiModelEditorPanel panel;

    private GuiTextElement name;
    private GuiTwoElement texture;
    private GuiThreeElement scale;
    private GuiTrackpadElement scaleGui;
    private GuiTextElement defaultTexture;
    private GuiButtonElement<GuiCheckBox> providesObj;
    private GuiButtonElement<GuiCheckBox> providesMtl;

    public GuiModelOptions(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc);

        this.createChildren();
        this.panel = panel;

        this.name = new GuiTextElement(mc, (str) -> this.panel.model.name = str, 120);
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
        this.scaleGui = new GuiTrackpadElement(mc, "GUI scale", (value) -> this.panel.model.scaleGui = value);
        this.defaultTexture = new GuiTextElement(mc, (str) -> this.panel.model.defaultTexture = new ResourceLocation(str), 1000);
        this.providesObj = GuiButtonElement.checkbox(mc, "Provides OBJ", false, (b) ->
        {
            this.panel.model.providesObj = b.button.isChecked();
            this.panel.rebuildModel();
        });
        this.providesMtl = GuiButtonElement.checkbox(mc, "Provides MTL", false, (b) ->
        {
            this.panel.model.providesMtl = b.button.isChecked();
            this.panel.rebuildModel();
        });

        int w = 120;

        this.name.resizer().parent(this.area).set(10, 20, w, 20);
        this.texture.resizer().set(0, 40, w, 20).relative(this.name.resizer());
        this.scale.resizer().set(0, 40, w, 20).relative(this.texture.resizer());
        this.scaleGui.resizer().set(0, 25, w, 20).relative(this.scale.resizer());
        this.defaultTexture.resizer().set(0, 40, w, 20).relative(this.scaleGui.resizer());
        this.providesObj.resizer().set(0, 25, w, 11).relative(this.defaultTexture.resizer());
        this.providesMtl.resizer().set(0, 16, w, 11).relative(this.providesObj.resizer());

        this.children.add(this.name, this.texture, this.scale, this.scaleGui, this.defaultTexture, this.providesObj, this.providesMtl);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton) || this.area.isInside(mouseX, mouseY);
    }

    public void fillData(Model model)
    {
        this.name.setText(model.name);
        this.texture.setValues(model.texture[0], model.texture[1]);
        this.scale.setValues(model.scale[0], model.scale[1], model.scale[2]);
        this.scaleGui.trackpad.setValue(model.scaleGui);
        this.defaultTexture.setText(model.defaultTexture == null ? "" : model.defaultTexture.toString());
        this.providesObj.button.setIsChecked(model.providesObj);
        this.providesMtl.button.setIsChecked(model.providesMtl);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);

        super.draw(mouseX, mouseY, partialTicks);

        this.font.drawStringWithShadow("Display name", this.name.area.x, this.name.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow("Texture size", this.texture.area.x, this.texture.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow("Global scale", this.scale.area.x, this.scale.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow("Default texture", this.defaultTexture.area.x, this.defaultTexture.area.y - 12, 0xeeeeee);
    }
}