package mchorse.blockbuster.recording.director.fake;

import java.util.Map;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public class FakeConfig implements ChannelConfig
{
    @Override
    public Map<ChannelOption<?>, Object> getOptions()
    {
        return null;
    }

    @Override
    public boolean setOptions(Map<ChannelOption<?>, ?> options)
    {
        return false;
    }

    @Override
    public <T> T getOption(ChannelOption<T> option)
    {
        return null;
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value)
    {
        return false;
    }

    @Override
    public int getConnectTimeoutMillis()
    {
        return 0;
    }

    @Override
    public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis)
    {
        return null;
    }

    @Override
    public int getMaxMessagesPerRead()
    {
        return 0;
    }

    @Override
    public ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead)
    {
        return null;
    }

    @Override
    public int getWriteSpinCount()
    {
        return 0;
    }

    @Override
    public ChannelConfig setWriteSpinCount(int writeSpinCount)
    {
        return null;
    }

    @Override
    public ByteBufAllocator getAllocator()
    {
        return null;
    }

    @Override
    public ChannelConfig setAllocator(ByteBufAllocator allocator)
    {
        return null;
    }

    @Override
    public RecvByteBufAllocator getRecvByteBufAllocator()
    {
        return null;
    }

    @Override
    public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator)
    {
        return null;
    }

    @Override
    public boolean isAutoRead()
    {
        return false;
    }

    @Override
    public ChannelConfig setAutoRead(boolean autoRead)
    {
        return null;
    }

    @Override
    public boolean isAutoClose()
    {
        return false;
    }

    @Override
    public ChannelConfig setAutoClose(boolean autoClose)
    {
        return null;
    }

    @Override
    public int getWriteBufferHighWaterMark()
    {
        return 0;
    }

    @Override
    public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark)
    {
        return null;
    }

    @Override
    public int getWriteBufferLowWaterMark()
    {
        return 0;
    }

    @Override
    public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark)
    {
        return null;
    }

    @Override
    public MessageSizeEstimator getMessageSizeEstimator()
    {
        return null;
    }

    @Override
    public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator)
    {
        return null;
    }
}