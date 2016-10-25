package mchorse.blockbuster.network.common.recording;

import java.util.List;

import mchorse.blockbuster.recording.data.Frame;

public class PacketFramesLoad extends PacketFrames
{
    public PacketFramesLoad()
    {}

    public PacketFramesLoad(String filename, List<Frame> frames)
    {
        super(filename, frames);
    }
}