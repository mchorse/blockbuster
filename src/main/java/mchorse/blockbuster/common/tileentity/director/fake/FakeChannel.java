package mchorse.blockbuster.common.tileentity.director.fake;

import java.net.SocketAddress;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public class FakeChannel implements Channel
{
    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key)
    {
        if (key == NetworkDispatcher.FML_DISPATCHER)
        {
            return (Attribute<T>) new FakeFMLAttribute();
        }

        if (key == NetworkManager.PROTOCOL_ATTRIBUTE_KEY)
        {
            return (Attribute<T>) new FakeProtocol();
        }

        return null;
    }

    @Override
    public int compareTo(Channel o)
    {
        return 0;
    }

    @Override
    public EventLoop eventLoop()
    {
        return null;
    }

    @Override
    public Channel parent()
    {
        return null;
    }

    @Override
    public ChannelConfig config()
    {
        return new FakeConfig();
    }

    @Override
    public boolean isOpen()
    {
        return false;
    }

    @Override
    public boolean isRegistered()
    {
        return false;
    }

    @Override
    public boolean isActive()
    {
        return false;
    }

    @Override
    public ChannelMetadata metadata()
    {
        return null;
    }

    @Override
    public SocketAddress localAddress()
    {
        return null;
    }

    @Override
    public SocketAddress remoteAddress()
    {
        return null;
    }

    @Override
    public ChannelFuture closeFuture()
    {
        return null;
    }

    @Override
    public boolean isWritable()
    {
        return false;
    }

    @Override
    public Unsafe unsafe()
    {
        return null;
    }

    @Override
    public ChannelPipeline pipeline()
    {
        return null;
    }

    @Override
    public ByteBufAllocator alloc()
    {
        return null;
    }

    @Override
    public ChannelPromise newPromise()
    {
        return null;
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise()
    {
        return null;
    }

    @Override
    public ChannelFuture newSucceededFuture()
    {
        return null;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause)
    {
        return null;
    }

    @Override
    public ChannelPromise voidPromise()
    {
        return null;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress)
    {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress)
    {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress)
    {
        return null;
    }

    @Override
    public ChannelFuture disconnect()
    {
        return null;
    }

    @Override
    public ChannelFuture close()
    {
        return null;
    }

    @Override
    public ChannelFuture deregister()
    {
        return null;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise)
    {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise)
    {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
    {
        return null;
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise)
    {
        return null;
    }

    @Override
    public ChannelFuture close(ChannelPromise promise)
    {
        return null;
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise)
    {
        return null;
    }

    @Override
    public Channel read()
    {
        return null;
    }

    @Override
    public ChannelFuture write(Object msg)
    {
        return null;
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise)
    {
        return null;
    }

    @Override
    public Channel flush()
    {
        return null;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise)
    {
        return null;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg)
    {
        return null;
    }
}