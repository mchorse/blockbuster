package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireInBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireNotInBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentKillPlane;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentParticleLifetime;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GuiSnowstormExpirationSection extends GuiSnowstormSection
{
	public GuiCirculateElement mode;
	public GuiTextElement expression;

	public GuiTrackpadElement a;
	public GuiTrackpadElement b;
	public GuiTrackpadElement c;
	public GuiTrackpadElement d;

	public GuiInventoryElement inventory;
	public GuiBlocksSection inBlocksSection;
	public GuiBlocksSection notInBlocksSection;

	private BedrockComponentParticleLifetime lifetime;
	private BedrockComponentKillPlane plane;
	private BedrockComponentExpireInBlocks inBlocks;
	private BedrockComponentExpireNotInBlocks notInBlocks;

	public GuiSnowstormExpirationSection(Minecraft mc)
	{
		super(mc);

		this.mode = new GuiCirculateElement(mc, (b) ->
		{
			this.lifetime.max = this.mode.getValue() == 1;
			this.updateTooltip();
		});
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.expiration.expression"));
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.expiration.max"));

		this.expression = new GuiTextElement(mc, 10000, (str) -> this.lifetime.expression = this.parse(str, this.expression, this.lifetime.expression));
		this.expression.tooltip(IKey.lang(""));

		this.a = new GuiTrackpadElement(mc, (value) -> this.plane.a = value);
		this.a.tooltip(IKey.str("Ax"));
		this.b = new GuiTrackpadElement(mc, (value) -> this.plane.b = value);
		this.b.tooltip(IKey.str("By"));
		this.c = new GuiTrackpadElement(mc, (value) -> this.plane.c = value);
		this.c.tooltip(IKey.str("Cz"));
		this.d = new GuiTrackpadElement(mc, (value) -> this.plane.d = value);
		this.d.tooltip(IKey.str("D"));

		this.inventory = new GuiInventoryElement(mc, (stack) ->
		{
			if (!(stack.getItem() instanceof ItemBlock))
			{
				this.inventory.linked.stack = ItemStack.EMPTY;
			}
			else
			{
				this.inventory.linked.stack = stack;
				this.inventory.unlink();
			}
		});
		this.inBlocksSection = new GuiBlocksSection(mc, IKey.lang("blockbuster.gui.snowstorm.expiration.in_blocks"), this);
		this.notInBlocksSection = new GuiBlocksSection(mc, IKey.lang("blockbuster.gui.snowstorm.expiration.not_in_blocks"), this);

		this.fields.add(Elements.row(mc, 5, 0, 20, Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F), this.mode));
		this.fields.add(this.expression);
		this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.expiration.kill_plane"), 20).anchor(0, 1F)
			.tooltip(IKey.lang("blockbuster.gui.snowstorm.expiration.kill_plane_tooltip")));
		this.fields.add(Elements.row(mc, 5, 0, 20, this.a, this.b));
		this.fields.add(Elements.row(mc, 5, 0, 20, this.c, this.d));
		this.fields.add(this.inBlocksSection, this.notInBlocksSection);
	}

	private void updateTooltip()
	{
		this.expression.tooltip.label.set(this.lifetime.max ? "blockbuster.gui.snowstorm.expiration.max_tooltip" : "blockbuster.gui.snowstorm.expiration.expression_tooltip");
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.expiration.title";
	}

	@Override
	public void setScheme(BedrockScheme scheme)
	{
		super.setScheme(scheme);

		this.lifetime = scheme.getOrCreate(BedrockComponentParticleLifetime.class);
		this.plane = scheme.getOrCreate(BedrockComponentKillPlane.class);
		this.inBlocks = scheme.getOrCreate(BedrockComponentExpireInBlocks.class);
		this.notInBlocks = scheme.getOrCreate(BedrockComponentExpireNotInBlocks.class);

		this.mode.setValue(this.lifetime.max ? 1 : 0);
		this.set(this.expression, this.lifetime.expression);
		this.updateTooltip();

		this.a.setValue(this.plane.a);
		this.b.setValue(this.plane.b);
		this.c.setValue(this.plane.c);
		this.d.setValue(this.plane.d);

		this.inventory.setVisible(false);

		if (!this.inventory.hasParent())
		{
			GuiElement element = this.getParentContainer();

			this.inventory.flex().relative(element).xy(0.5F, 0.5F).anchor(0.5F, 0.5F);
			this.inventory.resize();
			element.add(this.inventory);
		}

		this.inBlocksSection.setComponent(this.inBlocks);
		this.notInBlocksSection.setComponent(this.notInBlocks);
	}

	@Override
	public void beforeSave(BedrockScheme scheme)
	{
		this.compileBlocks(this.inBlocks, this.inBlocksSection);
		this.compileBlocks(this.notInBlocks, this.notInBlocksSection);
	}

	private void compileBlocks(BedrockComponentExpireBlocks component, GuiBlocksSection section)
	{
		component.blocks.clear();

		for (IGuiElement child : section.blocks.getChildren())
		{
			if (child instanceof GuiSlotElement)
			{
				GuiSlotElement slot = (GuiSlotElement) child;

				if (slot.stack.getItem() instanceof ItemBlock)
				{
					component.blocks.add(((ItemBlock) slot.stack.getItem()).getBlock());
				}
				else if (slot.stack.isEmpty())
				{
					component.blocks.add(Blocks.AIR);
				}
			}
		}
	}

	/**
	 * Blocks module
	 */
	public static class GuiBlocksSection extends GuiElement
	{
		public GuiElement blocks;

		private GuiSnowstormExpirationSection parent;
		private BedrockComponentExpireBlocks component;

		public GuiBlocksSection(Minecraft mc, IKey title, GuiSnowstormExpirationSection parent)
		{
			super(mc);

			this.parent = parent;

			GuiIconElement add = new GuiIconElement(mc, Icons.ADD, (b) ->
			{
				this.addBlock(Blocks.AIR);
				this.parent.resizeParent();
			});
			GuiLabel label = Elements.label(title).anchor(0, 0.5F);
			GuiElement row = Elements.row(mc, 5, 0, 20, label, add);
			this.blocks = new GuiElement(mc);


			add.flex().wh(10, 16);
			label.flex().h(0);
			row.flex().row(5).preferred(0);
			this.blocks.flex().grid(7).items(6).resizes(true);

			this.flex().column(5).vertical().stretch();
			this.add(row, this.blocks);
		}

		public void setComponent(BedrockComponentExpireBlocks component)
		{
			this.component = component;

			this.blocks.removeAll();

			for (Block block : this.component.blocks)
			{
				this.addBlock(block);
			}
		}

		public void addBlock(Block block)
		{
			GuiSlotElement slotElement = new GuiSlotElement(this.mc, 0, this.parent.inventory);

			slotElement.stack = new ItemStack(block, 1);
			slotElement.flex().wh(24, 24);
			slotElement.context(() -> new GuiSimpleContextMenu(this.mc).action(Icons.REMOVE, IKey.lang("Remove block"), () ->
			{
				slotElement.removeFromParent();
				this.parent.resizeParent();
			}));

			this.blocks.add(slotElement);
		}
	}
}