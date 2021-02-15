package mchorse.blockbuster.client.particles.molang.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.trig.Atan;

public class AtanDegrees extends Atan
{
    public AtanDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double get()
    {
        return super.get() / Math.PI * 180;
    }
}