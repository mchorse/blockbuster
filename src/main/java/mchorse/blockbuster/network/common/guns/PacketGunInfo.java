package mchorse.blockbuster.network.common.guns;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketGunInfo implements IMessage
{
    public NBTTagCompound tag;
    public int entity;

    public PacketGunInfo()
    {
        this.tag = new NBTTagCompound();
    }

    public PacketGunInfo(NBTTagCompound tag, int entity)
    {
        this.tag = tag;
        this.entity = entity;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.tag = NBTUtils.readInfiniteTag(buf);
        this.entity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, this.tag);
        buf.writeInt(this.entity);
    }
}