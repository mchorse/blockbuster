package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentInitialSpeed;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentInitialSpin;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentMotion;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentMotionDynamic;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentMotionParametric;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormMotionSection extends GuiSnowstormModeSection<BedrockComponentMotion>
{
	public GuiElement position;
	public GuiTextElement positionSpeed;
	public GuiTextElement positionX;
	public GuiTextElement positionY;
	public GuiTextElement positionZ;
	public GuiTextElement positionDrag;

	public GuiElement rotation;
	public GuiTextElement rotationAngle;
	public GuiTextElement rotationRate;
	public GuiTextElement rotationAcceleration;
	public GuiTextElement rotationDrag;

	private BedrockComponentInitialSpeed speed;
	private BedrockComponentInitialSpin spin;

	public GuiSnowstormMotionSection(Minecraft mc, GuiSnowstorm parent)
	{
		super(mc, parent);

		this.positionSpeed = new GuiTextElement(mc, 10000, (str) -> this.speed.speed = this.parse(str, this.positionSpeed, this.speed.speed));
		this.positionSpeed.tooltip(IKey.lang("blockbuster.gui.snowstorm.motion.position_speed"));
		this.positionX = new GuiTextElement(mc, 10000, (str) -> this.updatePosition(str, this.positionX, 0));
		this.positionX.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
		this.positionY = new GuiTextElement(mc, 10000, (str) -> this.updatePosition(str, this.positionY, 1));
		this.positionY.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
		this.positionZ = new GuiTextElement(mc, 10000, (str) -> this.updatePosition(str, this.positionZ, 2));
		this.positionZ.tooltip(IKey.lang("blockbuster.gui.model_block.z"));
		this.positionDrag = new GuiTextElement(mc, 10000, (str) ->
		{
			BedrockComponentMotionDynamic component = (BedrockComponentMotionDynamic) this.component;

			component.motionDrag = this.parse(str, this.positionDrag, component.motionDrag);
		});
		this.positionDrag.tooltip(IKey.lang("blockbuster.gui.snowstorm.motion.position_drag"));

		this.rotationAngle = new GuiTextElement(mc, 10000, (str) -> this.spin.rotation = this.parse(str, this.rotationAngle, this.spin.rotation));
		this.rotationAngle.tooltip(IKey.lang("blockbuster.gui.snowstorm.motion.rotation_angle"));
		this.rotationRate = new GuiTextElement(mc, 10000, (str) -> this.spin.rate = this.parse(str, this.rotationRate, this.spin.rate));
		this.rotationRate.tooltip(IKey.lang("blockbuster.gui.snowstorm.motion.rotation_speed"));
		this.rotationAcceleration = new GuiTextElement(mc, 10000, (str) ->
		{
			if (this.component instanceof BedrockComponentMotionDynamic)
			{
				BedrockComponentMotionDynamic component = (BedrockComponentMotionDynamic) this.component;

				component.rotationAcceleration = this.parse(str, this.rotationAcceleration, component.rotationAcceleration);
			}
			else
			{
				BedrockComponentMotionParametric component = (BedrockComponentMotionParametric) this.component;

				component.rotation = this.parse(str, this.rotationAcceleration, component.rotation);
			}
		});
		this.rotationAcceleration.tooltip(IKey.lang("blockbuster.gui.snowstorm.motion.rotation_acceleration"));
		this.rotationDrag = new GuiTextElement(mc, 10000, (str) ->
		{
			BedrockComponentMotionDynamic component = (BedrockComponentMotionDynamic) this.component;

			component.rotationDrag = this.parse(str, this.rotationDrag, component.rotationDrag);
		});
		this.rotationDrag.tooltip(IKey.lang("blockbuster.gui.snowstorm.motion.rotation_drag"));

		this.position = new GuiElement(mc);
		this.position.flex().column(5).vertical().stretch();
		this.position.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.motion.position"), 20).anchor(0, 1F), this.positionSpeed);
		this.position.add(this.positionX, this.positionY, this.positionZ);

		this.rotation = new GuiElement(mc);
		this.rotation.flex().column(5).vertical().stretch();
		this.rotation.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.motion.rotation"), 20).anchor(0, 1F), this.rotationAngle, this.rotationRate);
		this.rotation.add(this.rotationAcceleration);

		this.add(this.position, this.rotation);
	}

	private void updatePosition(String str, GuiTextElement element, int index)
	{
		if (this.component instanceof BedrockComponentMotionDynamic)
		{
			BedrockComponentMotionDynamic component = (BedrockComponentMotionDynamic) this.component;

			component.motionAcceleration[index] = this.parse(str, element, component.motionAcceleration[index]);
		}
		else
		{
			BedrockComponentMotionParametric component = (BedrockComponentMotionParametric) this.component;

			component.position[index] = this.parse(str, element, component.position[index]);
		}
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.motion.title";
	}

	@Override
	protected void fillModes(GuiCirculateElement button)
	{
		button.addLabel(IKey.lang("blockbuster.gui.snowstorm.motion.dynamic"));
		button.addLabel(IKey.lang("blockbuster.gui.snowstorm.motion.parametric"));
	}

	@Override
	protected Class<BedrockComponentMotion> getBaseClass()
	{
		return BedrockComponentMotion.class;
	}

	@Override
	protected Class getDefaultClass()
	{
		return BedrockComponentMotionDynamic.class;
	}

	@Override
	protected Class getModeClass(int value)
	{
		if (value == 1)
		{
			return BedrockComponentMotionParametric.class;
		}

		return BedrockComponentMotionDynamic.class;
	}

	@Override
	protected void fillData()
	{
		super.fillData();

		this.speed = this.scheme.getOrCreate(BedrockComponentInitialSpeed.class);
		this.spin = this.scheme.getOrCreate(BedrockComponentInitialSpin.class);

		this.set(this.positionSpeed, this.speed.speed);
		this.set(this.rotationAngle, this.spin.rotation);
		this.set(this.rotationRate, this.spin.rate);

		this.positionDrag.removeFromParent();
		this.rotationDrag.removeFromParent();

		if (this.component instanceof BedrockComponentMotionDynamic)
		{
			BedrockComponentMotionDynamic component = (BedrockComponentMotionDynamic) this.component;

			this.set(this.positionX, component.motionAcceleration[0]);
			this.set(this.positionY, component.motionAcceleration[1]);
			this.set(this.positionZ, component.motionAcceleration[2]);
			this.set(this.rotationAcceleration, component.rotationAcceleration);

			this.set(this.positionDrag, component.motionDrag);
			this.set(this.rotationDrag, component.rotationDrag);

			this.position.add(this.positionDrag);
			this.rotation.add(this.rotationDrag);
		}
		else
		{
			BedrockComponentMotionParametric component = (BedrockComponentMotionParametric) this.component;

			this.set(this.positionX, component.position[0]);
			this.set(this.positionY, component.position[1]);
			this.set(this.positionZ, component.position[2]);
			this.set(this.rotationAcceleration, component.rotation);
		}

		this.resizeParent();
	}
}