package mchorse.blockbuster.client.particles.molang.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class SinDegrees extends Function
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
    public double get()
    {
        return Math.sin(this.getArg(0) / 180 * Math.PI);
    }
}