package mchorse.blockbuster.core;

import net.minecraftforge.fml.common.DummyModContainer;

public class BBCoreModInfo extends DummyModContainer
{
    @Override
    public String getName()
    {
        return "Blockbuster Core mod";
    }

    @Override
    public String getModId()
    {
        return "blockbuster_core";
    }

    @Override
    public Object getMod()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return "@VERSION@";
    }
}