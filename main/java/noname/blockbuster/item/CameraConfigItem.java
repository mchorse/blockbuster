package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

/**
 * This item is needed to open camera's configuration GUI 
 */
public class CameraConfigItem extends Item 
{
	public CameraConfigItem()
	{
		setMaxStackSize(1);
		setUnlocalizedName("cameraConfigItem");
		setRegistryName("cameraConfigItem");
		setCreativeTab(Blockbuster.blockbusterTab);
	}
}