package mchorse.blockbuster.capabilities.gun;

import mchorse.blockbuster.common.GunProps;
import net.minecraft.item.ItemStack;

public class Gun implements IGun
{
    public GunProps props = new GunProps();

    public static IGun get(ItemStack stack)
    {
        return stack.getCapability(GunProvider.GUN, null);
    }

    @Override
    public GunProps getProps()
    {
        return this.props;
    }
}