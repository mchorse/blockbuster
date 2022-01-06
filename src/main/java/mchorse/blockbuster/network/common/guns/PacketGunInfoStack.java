package mchorse.blockbuster.network.common.guns;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PacketGunInfoStack implements IMessage
{
    public NBTTagCompound tag;
    public ItemStack stack;
    
    public PacketGunInfoStack()
    {
        this.tag = new NBTTagCompound();
    }
    
    public PacketGunInfoStack(NBTTagCompound tag, ItemStack stack)
    {
        this.tag = tag;
        this.stack = stack;
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.tag = NBTUtils.readInfiniteTag(buf);
        this.stack = ByteBufUtils.readItemStack(buf);
    }
    
    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, this.tag);
        ByteBufUtils.writeItemStack(buf,this.stack);
    }
}