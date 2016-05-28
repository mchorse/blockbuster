package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

public class RegisterItem extends Item
{
	public RegisterItem()
	{
		setMaxStackSize(1);
		setUnlocalizedName("registerItem");
		setRegistryName("registerItem");
		setCreativeTab(Blockbuster.blockbusterTab);
	}
}