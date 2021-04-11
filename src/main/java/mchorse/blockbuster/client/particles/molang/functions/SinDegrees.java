package mchorse.blockbuster.client.particles.molang.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.NNFunction;

public class SinDegrees extends NNFunction
{
    public SinDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double doubleValue()
    {
        return Math.sin(this.getArg(0).doubleValue() / 180 * Math.PI);
    }
}