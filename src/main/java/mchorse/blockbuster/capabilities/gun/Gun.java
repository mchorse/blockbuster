package mchorse.blockbuster.capabilities.gun;

import mchorse.blockbuster.common.GunInfo;
import net.minecraft.item.ItemStack;

public class Gun implements IGun
{
    public GunInfo info = new GunInfo();

    public static IGun get(ItemStack stack)
    {
        return stack.getCapability(GunProvider.GUN, null);
    }

    @Override
    public GunInfo getInfo()
    {
        return this.info;
    }
}