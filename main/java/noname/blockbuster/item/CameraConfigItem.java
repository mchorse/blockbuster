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
        this.setMaxStackSize(1);
        this.setUnlocalizedName("cameraConfigItem");
        this.setRegistryName("cameraConfigItem");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}