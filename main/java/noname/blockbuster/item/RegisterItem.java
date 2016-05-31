package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

public class RegisterItem extends Item
{
    public RegisterItem()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("registerItem");
        this.setRegistryName("registerItem");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}