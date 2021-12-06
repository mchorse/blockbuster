package mchorse.blockbuster.network.common.guns;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PacketGunInteract implements IMessage
{
    public ItemStack itemStack;
    public PacketGunInteract() {
    }
    public PacketGunInteract(ItemStack itemStack) {
        this.itemStack =itemStack;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.itemStack = ByteBufUtils.readItemStack(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        ByteBufUtils.writeItemStack(byteBuf,itemStack);
    }
}