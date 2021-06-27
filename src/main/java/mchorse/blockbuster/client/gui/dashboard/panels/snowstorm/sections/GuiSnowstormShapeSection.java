package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSectionManager;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeBase;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeBox;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeDisc;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeEntityAABB;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapePoint;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeSphere;
import mchorse.blockbuster.client.particles.components.shape.ShapeDirection;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormShapeSection extends GuiSnowstormModeSection<BedrockComponentShapeBase>
{
    public GuiTextElement offsetX;
    public GuiTextElement offsetY;
    public GuiTextElement offsetZ;
    public GuiDirectionSection direction;
    public GuiToggleElement surface;

    public GuiLabel radiusLabel;
    public GuiTextElement radius;

    public GuiLabel label;
    public GuiTextElement x;
    public GuiTextElement y;
    public GuiTextElement z;

    public GuiSnowstormShapeSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.offsetX = new GuiTextElement(mc, 10000, (str) -> this.component.offset[0] = this.parse(str, this.offsetX, this.component.offset[0]));
        this.offsetX.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
        this.offsetY = new GuiTextElement(mc, 10000, (str) -> this.component.offset[1] = this.parse(str, this.offsetY, this.component.offset[1]));
        this.offsetY.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
        this.offsetZ = new GuiTextElement(mc, 10000, (str) -> this.component.offset[2] = this.parse(str, this.offsetZ, this.component.offset[2]));
        this.offsetZ.tooltip(IKey.lang("blockbuster.gui.model_block.z"));
        this.direction = new GuiDirectionSection(mc, this);
        this.surface = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.shape.surface"), (b) ->
        {
            this.component.surface = b.isToggled();
            this.parent.dirty();
        });
        this.surface.tooltip(IKey.lang("blockbuster.gui.snowstorm.shape.surface_tooltip"));

        this.radiusLabel = Elements.label(IKey.lang("blockbuster.gui.snowstorm.shape.radius"));
        this.radiusLabel.marginTop(12);
        this.radius = new GuiTextElement(mc, 10000, (str) ->
        {
            BedrockComponentShapeSphere sphere = (BedrockComponentShapeSphere) this.component;

            sphere.radius = this.parse(str, this.radius, sphere.radius);
        });

        this.label = Elements.label(IKey.lang(""));
        this.label.marginTop(12);
        this.x = new GuiTextElement(mc, 10000, (str) -> this.updateNormalDimension(str, this.x, 0));
        this.x.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
        this.y = new GuiTextElement(mc, 10000, (str) -> this.updateNormalDimension(str, this.y, 1));
        this.y.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
        this.z = new GuiTextElement(mc, 10000, (str) -> this.updateNormalDimension(str, this.z, 2));
        this.z.tooltip(IKey.lang("blockbuster.gui.model_block.z"));

        this.modeLabel.label.set("blockbuster.gui.snowstorm.shape.shape");

        this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.shape.offset")).marginTop(12), this.offsetX, this.offsetY, this.offsetZ, this.direction, this.surface);
    }
    
    @Override
    protected void collapseState()
    {
        GuiSectionManager.setDefaultState(this.getClass().getSimpleName(), false);
        
        super.collapseState();
    }

    private void updateNormalDimension(String str, GuiTextElement element, int index)
    {
        if (this.component instanceof BedrockComponentShapeBox)
        {
            BedrockComponentShapeBox box = (BedrockComponentShapeBox) this.component;

            box.halfDimensions[index] = this.parse(str, element, box.halfDimensions[index]);
        }
        else if (this.component instanceof BedrockComponentShapeDisc)
        {
            BedrockComponentShapeDisc disc = (BedrockComponentShapeDisc) this.component;

            disc.normal[index] = this.parse(str, element, disc.normal[index]);
        }
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.shape.title";
    }

    @Override
    protected void fillModes(GuiCirculateElement button)
    {
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.point"));
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.box"));
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.sphere"));
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.disc"));
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.aabb"));
    }

    @Override
    protected void restoreInfo(BedrockComponentShapeBase component, BedrockComponentShapeBase old)
    {
        component.offset = old.offset;
        component.direction = old.direction;
        component.surface = old.surface;

        if (component instanceof BedrockComponentShapeSphere && old instanceof BedrockComponentShapeSphere)
        {
            ((BedrockComponentShapeSphere) component).radius = ((BedrockComponentShapeSphere) old).radius;
        }
    }

    @Override
    protected Class<BedrockComponentShapeBase> getBaseClass()
    {
        return BedrockComponentShapeBase.class;
    }

    @Override
    protected Class getDefaultClass()
    {
        return BedrockComponentShapePoint.class;
    }

    @Override
    protected Class getModeClass(int value)
    {
        if (value == 1)
        {
            return BedrockComponentShapeBox.class;
        }
        else if (value == 2)
        {
            return BedrockComponentShapeSphere.class;
        }
        else if (value == 3)
        {
            return BedrockComponentShapeDisc.class;
        }
        else if (value == 4)
        {
            return BedrockComponentShapeEntityAABB.class;
        }

        return BedrockComponentShapePoint.class;
    }

    @Override
    protected void fillData()
    {
        super.fillData();

        this.set(this.offsetX, this.component.offset[0]);
        this.set(this.offsetY, this.component.offset[1]);
        this.set(this.offsetZ, this.component.offset[2]);
        this.direction.fillData();
        this.surface.toggled(this.component.surface);

        if (this.component instanceof BedrockComponentShapeSphere)
        {
            this.set(this.radius, ((BedrockComponentShapeSphere) this.component).radius);
        }

        this.setNormalDimension(this.x, 0);
        this.setNormalDimension(this.y, 1);
        this.setNormalDimension(this.z, 2);

        this.radiusLabel.removeFromParent();;
        this.radius.removeFromParent();
        this.label.removeFromParent();
        this.x.removeFromParent();
        this.y.removeFromParent();
        this.z.removeFromParent();
        this.surface.removeFromParent();

        if (this.component instanceof BedrockComponentShapeSphere)
        {
            this.fields.add(this.radiusLabel, this.radius);
        }

        if (this.component instanceof BedrockComponentShapeBox || this.component instanceof BedrockComponentShapeDisc)
        {
            this.label.label.set("blockbuster.gui.snowstorm.shape." + (this.component instanceof BedrockComponentShapeBox ? "box_size" : "normal"));

            this.fields.add(this.label);
            this.fields.add(this.x);
            this.fields.add(this.y);
            this.fields.add(this.z);
        }

        this.fields.add(this.surface);

        this.resizeParent();
    }

    private void setNormalDimension(GuiTextElement text, int index)
    {
        if (this.component instanceof BedrockComponentShapeBox)
        {
            this.set(text, ((BedrockComponentShapeBox) this.component).halfDimensions[index]);
        }
        else if (this.component instanceof BedrockComponentShapeDisc)
        {
            this.set(text, ((BedrockComponentShapeDisc) this.component).normal[index]);
        }
    }

    public static class GuiDirectionSection extends GuiElement
    {
        public GuiSnowstormShapeSection parent;

        public GuiCirculateElement mode;
        public GuiTextElement x;
        public GuiTextElement y;
        public GuiTextElement z;

        public GuiDirectionSection(Minecraft mc, GuiSnowstormShapeSection parent)
        {
            super(mc);

            this.parent = parent;
            this.mode = new GuiCirculateElement(mc, (b) ->
            {
                int value = this.mode.getValue();

                if (value == 0)
                {
                    this.parent.component.direction = ShapeDirection.OUTWARDS;
                }
                else if (value == 1)
                {
                    this.parent.component.direction = ShapeDirection.INWARDS;
                }
                else
                {
                    this.parent.component.direction = new ShapeDirection.Vector(MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO);
                }

                this.parent.parent.dirty();
                this.fillData();
            });
            this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.direction_outwards"));
            this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.direction_inwards"));
            this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.shape.direction_vector"));

            this.x = new GuiTextElement(mc, 10000, (str) -> this.getVector().x = this.parent.parse(str, this.x, this.getVector().x));
            this.x.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
            this.y = new GuiTextElement(mc, 10000, (str) -> this.getVector().y = this.parent.parse(str, this.y, this.getVector().y));
            this.y.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
            this.z = new GuiTextElement(mc, 10000, (str) -> this.getVector().z = this.parent.parse(str, this.z, this.getVector().z));
            this.z.tooltip(IKey.lang("blockbuster.gui.model_block.z"));

            this.flex().column(5).vertical().stretch().height(20);
            this.add(Elements.row(mc, 5, 0, 20, Elements.label(IKey.lang("blockbuster.gui.snowstorm.shape.direction"), 20).anchor(0, 0.5F), this.mode));
        }

        private ShapeDirection.Vector getVector()
        {
            return (ShapeDirection.Vector) this.parent.component.direction;
        }

        public void fillData()
        {
            boolean isVector = this.parent.component.direction instanceof ShapeDirection.Vector;
            int value = 0;

            if (this.parent.component.direction == ShapeDirection.INWARDS)
            {
                value = 1;
            }
            else if (isVector)
            {
                value = 2;
            }

            this.mode.setValue(value);

            this.x.removeFromParent();
            this.y.removeFromParent();
            this.z.removeFromParent();

            if (isVector)
            {
                ShapeDirection.Vector vector = (ShapeDirection.Vector) this.parent.component.direction;

                this.parent.set(this.x, vector.x);
                this.parent.set(this.y, vector.y);
                this.parent.set(this.z, vector.z);

                this.add(this.x, this.y, this.z);
            }

            this.parent.resizeParent();
        }
    }
}
