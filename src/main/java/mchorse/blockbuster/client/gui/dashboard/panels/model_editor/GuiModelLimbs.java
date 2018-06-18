package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.api.Model.Limb.Holding;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiCirculate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelLimbs extends GuiElement
{
    public GuiModelEditorPanel panel;

    private GuiStringListElement limbList;

    private GuiThreeElement size;
    private GuiTwoElement texture;
    private GuiThreeElement anchor;
    private GuiThreeElement color;
    private GuiTrackpadElement opacity;
    private GuiButtonElement<GuiCheckBox> mirror;
    private GuiButtonElement<GuiCheckBox> lighting;
    private GuiButtonElement<GuiCheckBox> shading;
    private GuiButtonElement<GuiCheckBox> is3D;

    private GuiButtonElement<GuiCirculate> holding;
    private GuiButtonElement<GuiCheckBox> swiping;
    private GuiButtonElement<GuiCheckBox> looking;
    private GuiButtonElement<GuiCheckBox> swinging;
    private GuiButtonElement<GuiCheckBox> idle;
    private GuiButtonElement<GuiCheckBox> invert;

    public GuiThreeElement origin;

    public GuiModelLimbs(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc);

        this.createChildren();
        this.panel = panel;

        this.limbList = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbList.resizer().set(0, 0, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE);
        this.limbList.resizer().x.set(1, Measure.RELATIVE, -80);
        this.children.add(this.limbList);

        this.size = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.size[0] = values[0].intValue();
            this.panel.limb.size[1] = values[1].intValue();
            this.panel.limb.size[2] = values[2].intValue();
            this.panel.rebuildModel();
        });
        this.size.setLimit(1, 8192, true);
        this.texture = new GuiTwoElement(mc, (values) ->
        {
            this.panel.limb.texture[0] = values[0].intValue();
            this.panel.limb.texture[1] = values[1].intValue();
            this.panel.rebuildModel();
        });
        this.texture.setLimit(0, 8192, true);
        this.anchor = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.anchor[0] = values[0];
            this.panel.limb.anchor[1] = values[1];
            this.panel.limb.anchor[2] = values[2];
            this.panel.rebuildModel();
        });
        this.color = new GuiThreeElement(mc, (values) ->
        {
            this.panel.limb.color[0] = values[0];
            this.panel.limb.color[1] = values[1];
            this.panel.limb.color[2] = values[2];
        });
        this.color.setLimit(0, 1);
        this.opacity = new GuiTrackpadElement(mc, "Opacity", (value) -> this.panel.limb.opacity = value);
        this.opacity.setLimit(0, 1);
        this.mirror = GuiButtonElement.checkbox(mc, "Mirror", false, (b) ->
        {
            this.panel.limb.mirror = b.button.isChecked();
            this.panel.rebuildModel();
        });
        this.lighting = GuiButtonElement.checkbox(mc, "Lighting", false, (b) -> this.panel.limb.lighting = b.button.isChecked());
        this.shading = GuiButtonElement.checkbox(mc, "Shading", false, (b) -> this.panel.limb.shading = b.button.isChecked());
        this.is3D = GuiButtonElement.checkbox(mc, "3D", false, (b) -> this.panel.limb.is3D = b.button.isChecked());

        this.size.resizer().parent(this.area).set(10, 20, 120, 20);
        this.texture.resizer().relative(this.size.resizer()).set(0, 25, 120, 20);
        this.anchor.resizer().relative(this.texture.resizer()).set(0, 25, 120, 20);
        this.color.resizer().relative(this.anchor.resizer()).set(0, 35, 120, 20);
        this.opacity.resizer().relative(this.color.resizer()).set(0, 25, 120, 20);

        this.mirror.resizer().relative(this.opacity.resizer()).set(0, 25, 60, 11);
        this.is3D.resizer().relative(this.mirror.resizer()).set(0, 16, 60, 11);

        this.lighting.resizer().relative(this.opacity.resizer()).set(60, 25, 60, 11);
        this.shading.resizer().relative(this.lighting.resizer()).set(0, 16, 60, 11);

        this.children.add(this.size, this.texture, this.anchor, this.color, this.opacity, this.mirror, this.lighting, this.shading, this.is3D);

        /* Animation and shit */
        this.holding = new GuiButtonElement<GuiCirculate>(mc, new GuiCirculate(0, 0, 0, 0, 0), (b) -> this.panel.limb.holding = Holding.values()[b.button.getValue()]);
        this.holding.button.addLabel("None");
        this.holding.button.addLabel("Right");
        this.holding.button.addLabel("Left");
        this.swiping = GuiButtonElement.checkbox(mc, "Swiping", false, (b) -> this.panel.limb.swiping = b.button.isChecked());
        this.looking = GuiButtonElement.checkbox(mc, "Looking", false, (b) -> this.panel.limb.looking = b.button.isChecked());
        this.swinging = GuiButtonElement.checkbox(mc, "Swinging", false, (b) -> this.panel.limb.swinging = b.button.isChecked());
        this.idle = GuiButtonElement.checkbox(mc, "Idle", false, (b) -> this.panel.limb.idle = b.button.isChecked());
        this.invert = GuiButtonElement.checkbox(mc, "Invert", false, (b) -> this.panel.limb.invert = b.button.isChecked());

        this.holding.resizer().relative(this.is3D.resizer()).set(0, 25, 56, 20);
        this.swiping.resizer().relative(this.holding.resizer()).set(60, 6, 60, 11);

        this.looking.resizer().relative(this.holding.resizer()).set(0, 25, 60, 11);
        this.idle.resizer().relative(this.looking.resizer()).set(60, 0, 60, 11);
        this.swinging.resizer().relative(this.looking.resizer()).set(0, 16, 60, 11);
        this.invert.resizer().relative(this.swinging.resizer()).set(60, 0, 60, 11);

        this.children.add(this.holding, this.swiping, this.looking, this.swinging, this.idle, this.invert);
    }

    private void setLimb(String str)
    {
        this.panel.setLimb(str);
        this.fillLimbData(this.panel.limb);
    }

    public void setCurrent(String str)
    {
        this.limbList.setCurrent(str);
        this.fillLimbData(this.panel.limb);
    }

    public void fillData(Model model)
    {
        this.limbList.clear();
        this.limbList.add(model.limbs.keySet());
    }

    public void fillLimbData(Limb limb)
    {
        this.size.setValues(limb.size[0], limb.size[1], limb.size[2]);
        this.texture.setValues(limb.texture[0], limb.texture[1]);
        this.anchor.setValues(limb.anchor[0], limb.anchor[1], limb.anchor[2]);
        this.color.setValues(limb.color[0], limb.color[1], limb.color[2]);
        this.opacity.trackpad.setValue(limb.opacity);
        this.mirror.button.setIsChecked(limb.mirror);
        this.lighting.button.setIsChecked(limb.lighting);
        this.shading.button.setIsChecked(limb.shading);
        this.is3D.button.setIsChecked(limb.is3D);

        this.holding.button.setValue(limb.holding.ordinal());
        this.swiping.button.setIsChecked(limb.swiping);
        this.looking.button.setIsChecked(limb.looking);
        this.swinging.button.setIsChecked(limb.swinging);
        this.idle.button.setIsChecked(limb.idle);
        this.invert.button.setIsChecked(limb.invert);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);

        super.draw(mouseX, mouseY, partialTicks);

        this.font.drawStringWithShadow("Limbs", this.area.x + 10, this.area.y + 10, 0xeeeeee);
    }
}