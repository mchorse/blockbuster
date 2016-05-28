package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

public class RecordItem extends Item
{
	public RecordItem()
	{
		setMaxStackSize(1);
		setUnlocalizedName("recordItem");
		setRegistryName("recordItem");
		setCreativeTab(Blockbuster.blockbusterTab);
	}
}