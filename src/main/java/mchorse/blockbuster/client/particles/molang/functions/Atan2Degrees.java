package mchorse.blockbuster.client.particles.molang.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.trig.Atan2;

public class Atan2Degrees extends Atan2
{
    public Atan2Degrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}