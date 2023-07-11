package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSectionManager;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockMaterial;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceBillboard;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSnowstormGeneralSection extends GuiSnowstormSection
{
    public GuiTextElement identifier;
    public GuiButtonElement pick;
    public GuiCirculateElement material;
    public GuiTexturePicker texture;

    public GuiSnowstormGeneralSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.identifier = new GuiTextElement(mc, 100, (str) ->
        {
            this.scheme.identifier = str;
            this.parent.dirty();
        });
        this.identifier.tooltip(IKey.lang("blockbuster.gui.snowstorm.general.identifier"));

        this.pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.snowstorm.general.pick"), (b) ->
        {
            GuiElement container = this.getParentContainer();

            this.texture.fill(this.scheme.texture);
            this.texture.flex().relative(container).wh(1F, 1F);
            this.texture.resize();
            container.add(this.texture);
        });

        this.material = new GuiCirculateElement(mc, (b) ->
        {
            this.scheme.material = BedrockMaterial.values()[this.material.getValue()];
            this.parent.dirty();
        });
        this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_opaque"));
        this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_alpha"));
        this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_blend"));
        this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_additive"));

        this.texture = new GuiTexturePicker(mc, (rl) ->
        {
            if (rl == null)
            {
                rl = BedrockScheme.DEFAULT_TEXTURE;
            }

            this.setTextureSize(rl);
            this.scheme.texture = rl;
            this.parent.dirty();
        });

        this.fields.add(this.identifier, Elements.row(mc, 5, 0, 20, this.pick, this.material));
    }
    
    @Override
    protected void collapseState()
    {
        GuiSectionManager.setDefaultState(this.getClass().getSimpleName(), false);
        
        super.collapseState();
    }

    private void setTextureSize(ResourceLocation rl)
    {
        BedrockComponentAppearanceBillboard component = this.scheme.get(BedrockComponentAppearanceBillboard.class);

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
        return "blockbuster.gui.snowstorm.general.title";
    }

    @Override
    public void setScheme(BedrockScheme scheme)
    {
        super.setScheme(scheme);

        this.identifier.setText(scheme.identifier);
        this.material.setValue(scheme.material.ordinal());
    }
}
