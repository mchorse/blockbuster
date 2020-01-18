package mchorse.blockbuster.recording.scene.fake;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraft.network.EnumConnectionState;

public class FakeProtocol implements Attribute<EnumConnectionState>
{
    @Override
    public AttributeKey<EnumConnectionState> key()
    {
        return null;
    }

    @Override
    public EnumConnectionState get()
    {
        return null;
    }

    @Override
    public void set(EnumConnectionState value)
    {}

    @Override
    public EnumConnectionState getAndSet(EnumConnectionState value)
    {
        return null;
    }

    @Override
    public EnumConnectionState setIfAbsent(EnumConnectionState value)
    {
        return null;
    }

    @Override
    public EnumConnectionState getAndRemove()
    {
        return null;
    }

    @Override
    public boolean compareAndSet(EnumConnectionState oldValue, EnumConnectionState newValue)
    {
        return false;
    }

    @Override
    public void remove()
    {}
}