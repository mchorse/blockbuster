package mchorse.blockbuster.client.particles.molang.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;
import mchorse.mclib.math.functions.trig.Asin;

public class AsinDegrees extends Asin
{
    public AsinDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double get()
    {
        return super.get() / Math.PI * 180;
    }
}