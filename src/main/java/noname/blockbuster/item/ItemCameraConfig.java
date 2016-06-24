package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

/**
 * This item is needed to open camera's configuration GUI.
 *
 * See CameraEntity and GuiCamera for more information.
 */
public class ItemCameraConfig extends Item
{
    public ItemCameraConfig()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("camera_config");
        this.setUnlocalizedName("blockbuster.camera_config");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}