package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.mclib.utils.TextUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Chat action
 *
 * Sends chat message with some formatting.
 * See {@link ChatAction#apply(EntityLivingBase)} for more information.
 */
public class ChatAction extends Action
{
    public String message = "";

    public ChatAction()
    {}

    public ChatAction(String message)
    {
        this.message = message;
    }

    public String getMessage(EntityLivingBase actor)
    {
        String message = this.message;
        String prefix = Blockbuster.recordChatPrefix.get();

        if (!prefix.isEmpty())
        {
            message = prefix.replace("%NAME%", actor == null ? "Player" : actor.getName()) + message;
        }

        return TextUtils.processColoredText(message);
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        RecordUtils.broadcastMessage(this.getMessage(actor));
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