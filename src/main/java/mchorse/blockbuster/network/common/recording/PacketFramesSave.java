package mchorse.blockbuster.network.common.recording;

import java.util.List;

import mchorse.blockbuster.recording.data.Frame;

public class PacketFramesSave extends PacketFrames
{
    public PacketFramesSave()
    {}

    public PacketFramesSave(String filename, List<Frame> frames)
    {
        super(filename, frames);
    }
}
