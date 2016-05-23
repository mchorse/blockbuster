package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Main;

public class CameraConfigItem extends Item 
{
	public CameraConfigItem()
	{
		setMaxStackSize(1);
		setUnlocalizedName("cameraConfigItem");
		setRegistryName("cameraConfigItem");
		setCreativeTab(Main.busterTab);
	}
}
