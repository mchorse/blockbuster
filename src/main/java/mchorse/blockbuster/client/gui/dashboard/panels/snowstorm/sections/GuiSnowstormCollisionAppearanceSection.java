package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockMaterial;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentCollisionAppearance;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

public class GuiSnowstormCollisionAppearanceSection extends GuiSnowstormComponentSection<BedrockComponentCollisionAppearance>
{
    /* inheriting this throughout the structure would make things less copy and paste */
    public static final String GUI_PATH = "blockbuster.gui.snowstorm.appearance";
    
    public GuiToggleElement enabled;
    public GuiButtonElement pick;
    public GuiCirculateElement material;
    public GuiTexturePicker texture;
    
    public GuiCirculateElement mode;
    public GuiLabel modeLabel;

    public GuiCirculateElement facingMode;
    public GuiLabel facingModeLabel;
    public GuiTextElement sizeW;
    public GuiTextElement sizeH;
    public GuiTextElement uvX;
    public GuiTextElement uvY;
    public GuiTextElement uvW;
    public GuiTextElement uvH;

    public GuiElement flipbook;
    public GuiTrackpadElement stepX;
    public GuiTrackpadElement stepY;
    public GuiTrackpadElement fps;
    public GuiTextElement max;
    public GuiToggleElement stretch;
    public GuiToggleElement loop;
    
    public GuiSnowstormCollisionAppearanceSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.enabled"), (b) -> this.parent.dirty());
        
        this.pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.snowstorm.general.pick"), (b) ->
        {
            GuiElement container = this.getParentContainer();

            this.texture.fill(this.component.texture);
            this.texture.flex().relative(container).wh(1F, 1F);
            this.texture.resize();
            container.add(this.texture);
        });

        this.material = new GuiCirculateElement(mc, (b) ->
        {
            this.component.material = BedrockMaterial.values()[this.material.getValue()];
            this.parent.dirty();
        });
        this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_opaque"));
        this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_alpha"));
        this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_blend"));

        this.texture = new GuiTexturePicker(mc, (rl) ->
        {
            if (rl == null)
            {
                rl = BedrockScheme.DEFAULT_TEXTURE;
            }

            this.setTextureSize(rl);
            this.component.texture = rl;
            this.parent.dirty();
        });

        
        this.mode = new GuiCirculateElement(mc, (b) ->
        {
            this.component.flipbook = this.mode.getValue() == 1;
            this.updateElements();
            this.parent.dirty();
        });
        this.mode.addLabel(IKey.lang(GUI_PATH +".regular"));
        this.mode.addLabel(IKey.lang(GUI_PATH +".animated"));
        this.modeLabel = Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F);

        this.facingMode = new GuiCirculateElement(mc, (b) ->
        {
            this.component.facing = GuiSnowstormAppearanceSection.SORTED_FACING_MODES[this.facingMode.getValue()];
            this.parent.dirty();
        });
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_x"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_y"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_z"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.lookat_xyz"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.lookat_y"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.rotate_xyz"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.rotate_y"));
        this.facingModeLabel = Elements.label(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.label"), 20).anchor(0, 0.5F);
        
        this.sizeW = new GuiTextElement(mc, 10000, (str) -> this.component.sizeW = this.parse(str, this.sizeW, this.component.sizeW));
        this.sizeW.tooltip(IKey.lang(GUI_PATH +".width"));
        this.sizeH = new GuiTextElement(mc, 10000, (str) -> this.component.sizeH = this.parse(str, this.sizeH, this.component.sizeH));
        this.sizeH.tooltip(IKey.lang(GUI_PATH +".height"));

        this.uvX = new GuiTextElement(mc, 10000, (str) -> this.component.uvX = this.parse(str, this.uvX, this.component.uvX));
        this.uvX.tooltip(IKey.lang(GUI_PATH +".uv_x"));
        this.uvY = new GuiTextElement(mc, 10000, (str) -> this.component.uvY = this.parse(str, this.uvY, this.component.uvY));
        this.uvY.tooltip(IKey.lang(GUI_PATH +".uv_y"));
        this.uvW = new GuiTextElement(mc, 10000, (str) -> this.component.uvW = this.parse(str, this.uvW, this.component.uvW));
        this.uvW.tooltip(IKey.lang(GUI_PATH +".uv_w"));
        this.uvH = new GuiTextElement(mc, 10000, (str) -> this.component.uvH = this.parse(str, this.uvH, this.component.uvH));
        this.uvH.tooltip(IKey.lang(GUI_PATH +".uv_h"));

        this.stepX = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.stepX = value.floatValue();
            this.parent.dirty();
        });
        this.stepX.tooltip(IKey.lang(GUI_PATH +".step_x"));
        this.stepY = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.stepY = value.floatValue();
            this.parent.dirty();
        });
        this.stepY.tooltip(IKey.lang(GUI_PATH +".step_y"));
        this.fps = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.fps = value.floatValue();
            this.parent.dirty();
        });
        this.fps.tooltip(IKey.lang(GUI_PATH +".fps"));
        this.max = new GuiTextElement(mc, 10000, (str) -> this.component.maxFrame = this.parse(str, this.max, this.component.maxFrame));
        this.max.tooltip(IKey.lang(GUI_PATH +".max"));

        this.stretch = new GuiToggleElement(mc, IKey.lang(GUI_PATH +".stretch"), (b) ->
        {
            this.component.stretchFPS = b.isToggled();
            this.parent.dirty();
        });
        this.stretch.tooltip(IKey.lang(GUI_PATH +".stretch_tooltip"));
        this.loop = new GuiToggleElement(mc, IKey.lang(GUI_PATH +".loop"), (b) ->
        {
            this.component.loop = b.isToggled();
            this.parent.dirty();
        });
        this.loop.tooltip(IKey.lang(GUI_PATH +".loop_tooltip"));

        this.flipbook = new GuiElement(mc);
        this.flipbook.flex().column(5).vertical().stretch();
        this.flipbook.add(Elements.label(IKey.lang(GUI_PATH +".animated")).marginTop(12));
        this.flipbook.add(Elements.row(mc, 5, 0, 20, this.stepX, this.stepY));
        this.flipbook.add(Elements.row(mc, 5, 0, 20, this.fps, this.max));
        this.flipbook.add(Elements.row(mc, 5, 0, 20, this.stretch, this.loop));

        this.fields.add(this.enabled);
        this.fields.add(Elements.row(mc, 5, 0, 20, this.pick, this.material));
        this.fields.add(Elements.row(mc, 5, 0, 20, this.modeLabel, this.mode));
        this.fields.add(Elements.row(mc, 5, 0, 20, this.facingModeLabel, this.facingMode));
        this.fields.add(Elements.label(IKey.lang(GUI_PATH +".size")).marginTop(12));
        this.fields.add(this.sizeW, this.sizeH);
        this.fields.add(Elements.label(IKey.lang(GUI_PATH +".mapping")).marginTop(12));
        this.fields.add(this.uvX, this.uvY, this.uvW, this.uvH);
    }

    private void setTextureSize(ResourceLocation rl)
    {
        BedrockComponentCollisionAppearance component = this.scheme.get(BedrockComponentCollisionAppearance.class);

        if (component == null)
        {
            return;
        }

        this.mc.renderEngine.bindTexture(rl);

        component.textureWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        component.textureHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.collision.appearance.title";
    }

    @Override
    public void setScheme(BedrockScheme scheme)
    {
        super.setScheme(scheme);

        this.material.setValue(this.component.material.ordinal());
    }
    
    @Override
    public void beforeSave(BedrockScheme scheme)
    {
        this.component.enabled = this.enabled.isToggled() ? MolangParser.ONE : MolangParser.ZERO;
    }

    @Override
    protected BedrockComponentCollisionAppearance getComponent(BedrockScheme scheme) {
        return scheme.getOrCreate(BedrockComponentCollisionAppearance.class);
    }
    
    private void updateElements()
    {
        this.flipbook.removeFromParent();

        if (this.component.flipbook)
        {
            this.fields.add(this.flipbook);
        }

        this.resizeParent();
    }
    
    @Override
    protected void fillData()
    {
        super.fillData();

        this.enabled.toggled(MolangExpression.isOne(component.enabled));
        this.mode.setValue(this.component.flipbook ? 1 : 0);
        this.facingMode.setValue(ArrayUtils.indexOf(GuiSnowstormAppearanceSection.SORTED_FACING_MODES, this.component.facing));
        this.set(this.sizeW, this.component.sizeW);
        this.set(this.sizeH, this.component.sizeH);
        this.set(this.uvX, this.component.uvX);
        this.set(this.uvY, this.component.uvY);
        this.set(this.uvW, this.component.uvW);
        this.set(this.uvH, this.component.uvH);

        this.stepX.setValue(this.component.stepX);
        this.stepY.setValue(this.component.stepY);
        this.fps.setValue(this.component.fps);
        this.set(this.max, this.component.maxFrame);

        this.stretch.toggled(this.component.stretchFPS);
        this.loop.toggled(this.component.loop);

        this.updateElements();
    }
}
