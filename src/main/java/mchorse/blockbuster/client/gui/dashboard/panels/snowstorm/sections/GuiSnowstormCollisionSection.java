package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentMotionCollision;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Operation;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import net.minecraft.client.Minecraft;

public class GuiSnowstormCollisionSection extends GuiSnowstormComponentSection<BedrockComponentMotionCollision>
{
    public GuiTextElement condition;
    public GuiToggleElement realisticCollision;
    public GuiToggleElement entityCollision;
    public GuiToggleElement momentum;
    public GuiToggleElement realisticCollisionDrag;
    public GuiTrackpadElement drag;
    public GuiTrackpadElement bounciness;
    public GuiTrackpadElement randomBounciness; //randomize the direction vector
    public GuiToggleElement preserveEnergy;
    public GuiTrackpadElement randomDamp;
    public GuiTrackpadElement damp;
    public GuiTrackpadElement splitParticle; //split particle into n particles on collision
    public GuiTrackpadElement splitParticleSpeedThreshold;
    public GuiTrackpadElement radius;
    public GuiToggleElement expire;
    public GuiTextElement expirationDelay;
    
    public GuiElement controlToggleElements;
    public GuiElement randomBouncinessRow;

    private boolean wasPresent;
    private boolean updateButtons;

    public GuiSnowstormCollisionSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.condition = new GuiTextElement(mc, 10000, (str) ->
        {
            this.component.enabled = str.isEmpty() ? MolangParser.ONE : this.parse(str, this.condition, this.component.enabled);
            this.parent.dirty();
        });
        this.condition.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.condition_tooltip"));
        
        this.realisticCollision = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.realistic_collision"), (b) ->
        {
            this.component.realisticCollision = b.isToggled();
            this.parent.dirty();
        });
        
        this.entityCollision = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.entity_collision"), (b) ->
        {
            this.component.entityCollision = b.isToggled();
            this.parent.dirty();

            this.updateButtons();
        });
        
        this.momentum = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.momentum"), (b) ->
        {
            this.component.momentum = b.isToggled();
            this.parent.dirty();
        });

        this.realisticCollisionDrag = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.realistic_collision_drag"), (b) ->
        {
            this.component.realisticCollisionDrag = b.isToggled();
            this.parent.dirty();
        });
        this.realisticCollisionDrag.tooltip(IKey.lang("blockbuster.gui.snowstorm.realistic_collision_drag_tooltip"));
        
        this.drag = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.collisionDrag = value.floatValue();
            this.parent.dirty();
        });
        this.drag.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.drag"));
        
        this.bounciness = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.bounciness = value.floatValue();
            this.parent.dirty();

            this.updateButtons = true;
        });
        this.bounciness.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.bounciness"));
        
        this.randomBounciness = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.randomBounciness = (float) Math.abs(value);
            this.parent.dirty();

            this.updateButtons = true;
        });
        this.randomBounciness.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.random_direction"));
        
        this.preserveEnergy = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.preserve_energy"), (b) -> this.parent.dirty());
        this.preserveEnergy.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.preserve_energy_tooltip"));
        
        this.damp = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.damp = value.floatValue();
            this.parent.dirty();
        });
        this.damp.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.damping.strength"));
        this.damp.limit(0, 1);
        
        this.randomDamp = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.randomDamp = (float) Math.abs(value);
            this.parent.dirty();
        });
        this.randomDamp.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.damping.randomness"));
        this.randomDamp.limit(0, 1);
        
        this.splitParticleSpeedThreshold = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.splitParticleSpeedThreshold = value.floatValue();
            this.parent.dirty();
        });
        this.splitParticleSpeedThreshold.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.split_particle.speed_threshold"));
        
        this.splitParticle = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.splitParticleCount = (int)Math.abs(value);
            this.parent.dirty();
        });
        this.splitParticle.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.split_particle.count"));
        this.splitParticle.limit(0, 99).integer();
        
        this.radius = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.radius = value.floatValue();
            this.parent.dirty();
        });
        this.radius.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.radius"));
        
        this.expire = new GuiToggleElement(mc,  IKey.lang("blockbuster.gui.snowstorm.collision.expire"), (b) ->
        {
            this.component.expireOnImpact = b.isToggled();
            this.parent.dirty();
        });
        
        this.expirationDelay = new GuiTextElement(mc, 10000, (value) ->
        {
            this.component.expirationDelay = this.parse(value, this.expirationDelay, this.component.expirationDelay);
            this.parent.dirty();
        });
        this.expirationDelay.tooltip(IKey.lang("blockbuster.gui.snowstorm.collision.expiration_delay"));
        
        this.controlToggleElements = new GuiElement(mc);
        this.controlToggleElements.flex().column(4).stretch().vertical().height(4);
        
        this.controlToggleElements.add(this.condition, this.realisticCollision, this.entityCollision);
        
        this.randomBouncinessRow = new GuiElement(mc);
        this.randomBouncinessRow.flex().column(2).stretch().vertical().height(2);

        this.randomBouncinessRow.add(this.randomBounciness);
        
        this.fields.add(this.controlToggleElements, this.realisticCollisionDrag, this.drag, this.bounciness, this.randomBouncinessRow , this.radius, this.expire, this.expirationDelay);
        this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.collision.damping.title")).marginTop(12), this.damp, this.randomDamp);
        this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.collision.split_particle.title")).marginTop(12), this.splitParticle,  this.splitParticleSpeedThreshold);
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.collision.title";
    }

    @Override
    public void beforeSave(BedrockScheme scheme)
    {
        this.component.preserveEnergy = this.preserveEnergy.isToggled();
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
        this.set(this.condition, this.component.enabled);
        this.realisticCollision.toggled(this.component.realisticCollision);
        this.entityCollision.toggled(this.component.entityCollision);
        this.momentum.toggled(this.component.momentum);
        this.realisticCollisionDrag.toggled(this.component.realisticCollisionDrag);
        this.drag.setValue(this.component.collisionDrag);
        this.bounciness.setValue(this.component.bounciness);
        this.randomBounciness.setValue(this.component.randomBounciness);
        this.preserveEnergy.toggled(this.component.preserveEnergy);
        this.damp.setValue(this.component.damp);
        this.randomDamp.setValue(this.component.randomDamp);
        this.splitParticle.setValue(this.component.splitParticleCount);
        this.splitParticleSpeedThreshold.setValue(this.component.splitParticleSpeedThreshold);
        this.radius.setValue(this.component.radius);
        this.expire.toggled(this.component.expireOnImpact);
        this.set(this.expirationDelay, this.component.expirationDelay);
        
        this.updateButtons();
    }

    private void updateButtons()
    {
        this.preserveEnergy.removeFromParent();
        this.momentum.removeFromParent();

        if (this.entityCollision.isToggled())
        {
            this.controlToggleElements.add(this.momentum);
        }

        if (!Operation.equals(this.randomBounciness.value, 0) && Operation.equals(this.bounciness.value, 0))
        {
            this.randomBouncinessRow.add(this.preserveEnergy);
        }

        this.resizeParent();
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        if (this.updateButtons)
        {
            this.updateButtons();
            this.updateButtons = false;
        }
    }
}
