package mchorse.blockbuster_pack.client.render.part;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Interface for body-part system part
 */
@SideOnly(Side.CLIENT)
public interface IBodyPart
{
    public void init();

    public void render(float partialTicks);

    public void update();

    public void toNBT(NBTTagCompound tag);

    public void fromNBT(NBTTagCompound tag);
}