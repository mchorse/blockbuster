package mchorse.blockbuster.recording.scene.fake;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public class FakeFMLAttribute implements Attribute<NetworkDispatcher>
{
    @Override
    public AttributeKey<NetworkDispatcher> key()
    {
        return null;
    }

    @Override
    public NetworkDispatcher get()
    {
        return null;
    }

    @Override
    public void set(NetworkDispatcher value)
    {}

    @Override
    public NetworkDispatcher getAndSet(NetworkDispatcher value)
    {
        return null;
    }

    @Override
    public NetworkDispatcher setIfAbsent(NetworkDispatcher value)
    {
        return null;
    }

    @Override
    public NetworkDispatcher getAndRemove()
    {
        return null;
    }

    @Override
    public boolean compareAndSet(NetworkDispatcher oldValue, NetworkDispatcher newValue)
    {
        return false;
    }

    @Override
    public void remove()
    {}
}