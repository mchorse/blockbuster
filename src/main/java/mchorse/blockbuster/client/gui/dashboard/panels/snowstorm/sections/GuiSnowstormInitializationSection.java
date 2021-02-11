package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentInitialization;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormInitializationSection extends GuiSnowstormComponentSection<BedrockComponentInitialization>
{
	public GuiTextElement create;
	public GuiTextElement update;
	public GuiTextElement createLocal;
    public GuiTextElement updateLocal;

	public GuiSnowstormInitializationSection(Minecraft mc, GuiSnowstorm parent)
	{
		super(mc, parent);

		this.create = new GuiTextElement(mc, 10000, (str) -> this.component.creation = this.parse(str, this.create, this.component.creation));
		this.create.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.create"));
		
		this.update = new GuiTextElement(mc, 10000, (str) -> this.component.update = this.parse(str, this.update, this.component.update));
		this.update.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.update"));
        
		this.createLocal = new GuiTextElement(mc, 10000, (str) -> this.component.localCreation = this.parse(str, this.createLocal, this.component.localCreation));
        this.createLocal.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.create_local"));
        
        this.updateLocal = new GuiTextElement(mc, 10000, (str) -> this.component.localUpdate = this.parse(str, this.updateLocal, this.component.localUpdate));
        this.updateLocal.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.update_local"));
        
		
		this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.initialization.emitter_variables"), 20).anchor(0, 1F), this.create, this.update);
		this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.initialization.particle_variables"), 20).anchor(0, 1F), this.createLocal, this.updateLocal);
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.initialization.title";
	}

	@Override
	protected BedrockComponentInitialization getComponent(BedrockScheme scheme)
	{
		return this.scheme.getOrCreate(BedrockComponentInitialization.class);
	}

	@Override
	protected void fillData()
	{
		this.set(this.create, this.component.creation);
		this.set(this.update, this.component.update);
		this.set(this.createLocal, this.component.localCreation);
        this.set(this.updateLocal, this.component.localUpdate);
	}
}