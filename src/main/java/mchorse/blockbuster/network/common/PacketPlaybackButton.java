package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.network.common.scene.PacketScene;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

public class PacketPlaybackButton extends PacketScene
{
    public int mode;
    public String profile = "";
    public List<String> scenes = new ArrayList<String>();

    public PacketPlaybackButton()
    {}

    public PacketPlaybackButton(SceneLocation location, int mode, String profile)
    {
        super(location);
        this.mode = mode;
        this.profile = profile;
    }

    public PacketPlaybackButton withScenes(List<String> scenes)
    {
        this.scenes.addAll(scenes);

        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.mode = buf.readInt();
        this.profile = ByteBufUtils.readUTF8String(buf);

        int count = buf.readInt();

        for (int i = 0; i < count; i++)
        {
            this.scenes.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeInt(this.mode);
        ByteBufUtils.writeUTF8String(buf, this.profile);

        buf.writeInt(this.scenes.size());

        for (String scene : this.scenes)
        {
            ByteBufUtils.writeUTF8String(buf, scene);
        }
    }
}