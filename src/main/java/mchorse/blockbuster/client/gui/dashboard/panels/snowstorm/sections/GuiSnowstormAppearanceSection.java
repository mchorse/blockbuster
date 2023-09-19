package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSectionManager;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceBillboard;
import mchorse.blockbuster.client.particles.components.appearance.CameraFacing;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;

public class GuiSnowstormAppearanceSection extends GuiSnowstormComponentSection<BedrockComponentAppearanceBillboard>
{
    public static final CameraFacing[] SORTED_FACING_MODES = {CameraFacing.DIRECTION_X, CameraFacing.DIRECTION_Y, CameraFacing.DIRECTION_Z, CameraFacing.LOOKAT_XYZ, CameraFacing.LOOKAT_Y, CameraFacing.LOOKAT_DIRECTION, CameraFacing.ROTATE_XYZ, CameraFacing.ROTATE_Y, CameraFacing.EMITTER_XY, CameraFacing.EMITTER_XZ, CameraFacing.EMITTER_YZ};

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
    public GuiElement modeRow;
    public GuiElement directionContainer;
    public GuiCirculateElement directionMode;
    public GuiTrackpadElement speedThreshold;
    public GuiTextElement directionX;
    public GuiTextElement directionY;
    public GuiTextElement directionZ;

    public GuiElement flipbook;
    public GuiTrackpadElement stepX;
    public GuiTrackpadElement stepY;
    public GuiTrackpadElement fps;
    public GuiTextElement max;
    public GuiToggleElement stretch;
    public GuiToggleElement loop;
    
    public GuiSnowstormAppearanceSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.mode = new GuiCirculateElement(mc, (b) ->
        {
            this.component.flipbook = this.mode.getValue() == 1;
            this.updateElements();
            this.parent.dirty();
        });
        this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.regular"));
        this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.animated"));
        this.modeLabel = Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F);
        
        this.facingMode = new GuiCirculateElement(mc, (b) ->
        {
            this.component.facing = SORTED_FACING_MODES[this.facingMode.getValue()];
            this.updateElements();
            this.parent.dirty();
        });
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_x"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_y"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_z"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.lookat_xyz"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.lookat_y"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.lookat_direction"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.rotate_xyz"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.rotate_y"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.emitter_xy"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.emitter_xz"));
        this.facingMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.emitter_yz"));
        this.facingModeLabel = Elements.label(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.label"), 20).anchor(0, 0.5F);

        this.directionContainer = new GuiElement(mc);
        this.directionContainer.flex().column(5).vertical().stretch();
        this.directionMode = new GuiCirculateElement(mc, (b) -> {
            this.component.customDirection = b.getValue() == 1;
            this.updateElements();
            this.parent.dirty();
        });
        this.directionMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_mode.motion"));
        this.directionMode.addLabel(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_mode.custom"));

        this.directionX = new GuiTextElement(mc, 10000, (str) -> {
            this.component.directionX = this.parse(str, this.directionX, this.component.directionX);
        });
        this.directionX.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_mode.direction_x_tooltip"));

        this.directionY = new GuiTextElement(mc, 10000, (str) -> {
            this.component.directionY = this.parse(str, this.directionY, this.component.directionY);
        });
        this.directionX.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_mode.direction_y_tooltip"));

        this.directionZ = new GuiTextElement(mc, 10000, (str) -> {
            this.component.directionZ = this.parse(str, this.directionZ, this.component.directionZ);
        });
        this.directionX.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_mode.direction_z_tooltip"));
        this.speedThreshold = new GuiTrackpadElement(mc, (value) -> {
            this.component.directionSpeedThreshhold = value.floatValue();
            this.parent.dirty();
        });
        this.speedThreshold.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.camera_facing.direction_mode.speed_threshold_tooltip"));

        this.directionContainer.add(this.directionMode, this.speedThreshold);
        
        this.sizeW = new GuiTextElement(mc, 10000, (str) -> this.component.sizeW = this.parse(str, this.sizeW, this.component.sizeW));
        this.sizeW.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.width"));
        this.sizeH = new GuiTextElement(mc, 10000, (str) -> this.component.sizeH = this.parse(str, this.sizeH, this.component.sizeH));
        this.sizeH.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.height"));

        this.uvX = new GuiTextElement(mc, 10000, (str) -> this.component.uvX = this.parse(str, this.uvX, this.component.uvX));
        this.uvX.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.uv_x"));
        this.uvY = new GuiTextElement(mc, 10000, (str) -> this.component.uvY = this.parse(str, this.uvY, this.component.uvY));
        this.uvY.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.uv_y"));
        this.uvW = new GuiTextElement(mc, 10000, (str) -> this.component.uvW = this.parse(str, this.uvW, this.component.uvW));
        this.uvW.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.uv_w"));
        this.uvH = new GuiTextElement(mc, 10000, (str) -> this.component.uvH = this.parse(str, this.uvH, this.component.uvH));
        this.uvH.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.uv_h"));

        this.stepX = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.stepX = value.floatValue();
            this.parent.dirty();
        });
        this.stepX.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.step_x"));
        this.stepY = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.stepY = value.floatValue();
            this.parent.dirty();
        });
        this.stepY.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.step_y"));
        this.fps = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.fps = value.floatValue();
            this.parent.dirty();
        });
        this.fps.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.fps"));
        this.max = new GuiTextElement(mc, 10000, (str) -> this.component.maxFrame = this.parse(str, this.max, this.component.maxFrame));
        this.max.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.max"));

        this.stretch = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.appearance.stretch"), (b) ->
        {
            this.component.stretchFPS = b.isToggled();
            this.parent.dirty();
        });
        this.stretch.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.stretch_tooltip"));
        this.loop = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.appearance.loop"), (b) ->
        {
            this.component.loop = b.isToggled();
            this.parent.dirty();
        });
        this.loop.tooltip(IKey.lang("blockbuster.gui.snowstorm.appearance.loop_tooltip"));

        this.modeRow = Elements.row(mc, 5, 0, 20, this.facingModeLabel, this.facingMode);

        this.flipbook = new GuiElement(mc);
        this.flipbook.flex().column(5).vertical().stretch();
        this.flipbook.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.appearance.animated")).marginTop(12));
        this.flipbook.add(Elements.row(mc, 5, 0, 20, this.stepX, this.stepY));
        this.flipbook.add(Elements.row(mc, 5, 0, 20, this.fps, this.max));
        this.flipbook.add(Elements.row(mc, 5, 0, 20, this.stretch, this.loop));

        this.fields.add(Elements.row(mc, 5, 0, 20, this.modeLabel, this.mode));
        this.fields.add(this.modeRow);
        this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.appearance.size")).marginTop(12));
        this.fields.add(this.sizeW, this.sizeH);
        this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.appearance.mapping")).marginTop(12));
        this.fields.add(this.uvX, this.uvY, this.uvW, this.uvH);
    }
    
    @Override
    protected void collapseState()
    {
        GuiSectionManager.setDefaultState(this.getClass().getSimpleName(), false);
        
        super.collapseState();
    }

    private void updateElements()
    {
        this.directionContainer.removeFromParent();

        if (this.component.facing.isDirection)
        {
            this.fields.addAfter(this.modeRow, this.directionContainer);
            this.speedThreshold.removeFromParent();
            this.directionX.removeFromParent();
            this.directionY.removeFromParent();
            this.directionZ.removeFromParent();

            if (this.component.customDirection)
            {
                this.directionContainer.add(this.directionX, this.directionY, this.directionZ);
            }
            else
            {
                this.directionContainer.add(this.speedThreshold);
            }
        }
        else
        {
            this.directionContainer.removeFromParent();
        }

        this.flipbook.removeFromParent();

        if (this.component.flipbook)
        {
            this.fields.add(this.flipbook);
        }

        this.resizeParent();
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.appearance.title";
    }

    @Override
    protected BedrockComponentAppearanceBillboard getComponent(BedrockScheme scheme)
    {
        return scheme.getOrCreateExact(BedrockComponentAppearanceBillboard.class);
    }

    @Override
    protected void fillData()
    {
        super.fillData();

        this.mode.setValue(this.component.flipbook ? 1 : 0);
        this.facingMode.setValue(ArrayUtils.indexOf(SORTED_FACING_MODES, this.component.facing));
        this.set(this.sizeW, this.component.sizeW);
        this.set(this.sizeH, this.component.sizeH);
        this.set(this.uvX, this.component.uvX);
        this.set(this.uvY, this.component.uvY);
        this.set(this.uvW, this.component.uvW);
        this.set(this.uvH, this.component.uvH);
        this.set(this.directionX, this.component.directionX);
        this.set(this.directionY, this.component.directionY);
        this.set(this.directionZ, this.component.directionZ);
        this.speedThreshold.setValue(this.component.directionSpeedThreshhold);
        this.directionMode.setValue(this.component.customDirection ? 1 : 0);

        this.stepX.setValue(this.component.stepX);
        this.stepY.setValue(this.component.stepY);
        this.fps.setValue(this.component.fps);
        this.set(this.max, this.component.maxFrame);

        this.stretch.toggled(this.component.stretchFPS);
        this.loop.toggled(this.component.loop);

        this.updateElements();
    }
}
