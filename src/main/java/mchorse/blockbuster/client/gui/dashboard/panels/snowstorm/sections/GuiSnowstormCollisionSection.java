package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentMotionCollision;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormCollisionSection extends GuiSnowstormComponentSection<BedrockComponentMotionCollision>
{
	public GuiToggleElement enabled;
	public GuiToggleElement realisticCollision;
	public GuiTrackpadElement drag;
	public GuiTrackpadElement bounciness;
	public GuiTrackpadElement randomBounciness;
	public GuiTrackpadElement randomDamp;
	public GuiTrackpadElement damp;
	public GuiTrackpadElement splitParticle; //split particle into n particles on collision
	public GuiTrackpadElement splitParticleSpeedThreshold;
	public GuiTrackpadElement radius;
	public GuiToggleElement expire;

	private boolean wasPresent;

	public GuiSnowstormCollisionSection(Minecraft mc, GuiSnowstorm parent)
	{
		super(mc, parent);

		this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.enabled"), (b) -> this.parent.dirty());
		
		this.realisticCollision = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.realisticCollision"), (b) ->
		{
			this.component.realisticCollision = b.isToggled();
			this.parent.dirty();
		});
		
		this.drag = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.collissionDrag = value.floatValue();
			this.parent.dirty();
		});
		this.drag.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.drag"));
		
		this.bounciness = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.bounciness = value.floatValue();
			this.parent.dirty();
		});
		this.bounciness.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.bounciness"));
		
		this.randomBounciness = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.randomBounciness = (float) Math.abs(value);
			this.parent.dirty();
		});
		this.randomBounciness.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.randomBounciness"));
		
		this.damp = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.damp = value.floatValue();
			this.parent.dirty();
		});
		this.damp.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.damp"));

		this.randomDamp = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.randomDamp = (float) Math.abs(value);
			this.parent.dirty();
		});
		this.randomDamp.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.randomDamp"));
		
		this.splitParticleSpeedThreshold = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.splitParticleSpeedThreshold = value.floatValue();
			this.parent.dirty();
		});
		this.splitParticleSpeedThreshold.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.splitParticleSpeedThreshold"));
		
		this.splitParticle = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.splitParticleCount = (int)Math.abs(value);
			this.parent.dirty();
		});
		this.splitParticle.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.splitParticle"));
		
		this.radius = new GuiTrackpadElement(mc, (value) ->
		{
			this.component.radius = value.floatValue();
			this.parent.dirty();
		});
		this.radius.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.radius"));
		
		this.expire = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.expire"), (b) ->
		{
			this.component.expireOnImpact = b.isToggled();
			this.parent.dirty();
		});

		this.fields.add(this.enabled, this.realisticCollision, this.drag, this.bounciness, this.randomBounciness,
						this.damp, this.randomDamp, this.splitParticle,  this.splitParticleSpeedThreshold, this.radius, this.expire);
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.collision.title";
	}

	@Override
	public void beforeSave(BedrockScheme scheme)
	{
		this.component.enabled = this.enabled.isToggled() ? MolangParser.ONE : MolangParser.ZERO;
	}

	@Override
	protected BedrockComponentMotionCollision getComponent(BedrockScheme scheme)
	{
		this.wasPresent = this.scheme.get(BedrockComponentMotionCollision.class) != null;

		return scheme.getOrCreate(BedrockComponentMotionCollision.class);
	}

	@Override
	protected void fillData()
	{
		this.enabled.toggled(this.wasPresent);
		this.realisticCollision.toggled(this.component.realisticCollision);
		this.drag.setValue(this.component.collissionDrag);
		this.bounciness.setValue(this.component.bounciness);
		this.randomBounciness.setValue(this.component.randomBounciness);
		this.damp.setValue(this.component.damp);
		this.randomDamp.setValue(this.component.randomDamp);
		this.splitParticle.setValue(this.component.splitParticleCount);
		this.splitParticleSpeedThreshold.setValue(this.component.splitParticleSpeedThreshold);
		this.radius.setValue(this.component.radius);
		this.expire.toggled(this.component.expireOnImpact);
	}
}
