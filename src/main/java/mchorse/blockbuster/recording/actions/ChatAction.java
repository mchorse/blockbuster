package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Chat action
 *
 * Sends chat message with some formatting.
 * See {@link ChatAction#apply(mchorse.blockbuster.common.entity.EntityActor)} for more
 * information.
 */
public class ChatAction extends Action
{
    public String message;

    public ChatAction()
    {}

    public ChatAction(String message)
    {
        this.message = message;
    }

    @Override
    public byte getType()
    {
        return Action.CHAT;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        Utils.broadcastMessage(this.message.replace('[', '§'));
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);
        ByteBufUtils.writeUTF8String(buf, this.message);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.message = tag.getString("Message");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Message", this.message);
    }
}