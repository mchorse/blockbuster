package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireInBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireNotInBlocks;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
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
	public GuiInventoryElement inventory;
	public GuiBlocksSection inBlocksSection;
	public GuiBlocksSection notInBlocksSection;

	private BedrockComponentExpireInBlocks inBlocks;
	private BedrockComponentExpireNotInBlocks notInBlocks;

	public GuiSnowstormExpirationSection(Minecraft mc)
	{
		super(mc);

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

		this.fields.add(this.inBlocksSection, this.notInBlocksSection);
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

		this.inventory.setVisible(false);
		this.inBlocks = scheme.getOrCreate(BedrockComponentExpireInBlocks.class);
		this.notInBlocks = scheme.getOrCreate(BedrockComponentExpireNotInBlocks.class);

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

			row.flex().row(5).preferred(0);
			label.flex().h(0);
			this.blocks = new GuiElement(mc);
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