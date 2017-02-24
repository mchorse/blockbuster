package mchorse.blockbuster.camera;

import net.minecraft.util.math.MathHelper;

/**
 * Interpolation methods
 *
 * This class is responsible for doing different kind of interpolations. Cubic
 * interpolation code was from website below, but BauerCam also uses this code.
 *
 * @author mchorse
 * @link http://paulbourke.net/miscellaneous/interpolation/
 * @link https://github.com/daipenger/BauerCam
 */
public class Interpolations
{
    /**
     * Linear interpolation
     */
    public static float lerp(float a, float b, float position)
    {
        return a + (b - a) * position;
    }

    /**
     * Special interpolation method for interpolating yaw. The problem with yaw,
     * is that it may go in the "wrong" direction when having, for example,
     * -170 (as a) and 170 (as b) degress or other way around (170 and -170).
     *
     * This interpolation method fixes this problem.
     */
    public static float lerpYaw(float a, float b, float position)
    {
        a = MathHelper.wrapDegrees(a);
        b = MathHelper.wrapDegrees(b);

        return lerp(a, normalizeYaw(a, b), position);
    }

    /**
     * Yaw normalization for cubic interpolation
     */
    public static float cubicYaw(float y0, float y1, float y2, float y3, float position)
    {
        y0 = MathHelper.wrapDegrees(y0);
        y1 = MathHelper.wrapDegrees(y1);
        y2 = MathHelper.wrapDegrees(y2);
        y3 = MathHelper.wrapDegrees(y3);

        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);

        return cubic(y0, y1, y2, y3, position);
    }

    /**
     * Normalize yaw rotation (argument {@code b}) based on the previous
     * yaw rotation.
     */
    public static float normalizeYaw(float a, float b)
    {
        float diff = a - b;

        if (diff > 180 || diff < -180)
        {
            diff = Math.copySign(360 - Math.abs(diff), diff);

            return a + diff;
        }

        return b;
    }

    /**
     * Cubic interpolation using Hermite between y1 and y2. Taken from paul's
     * website.
     *
     * @param y0 - points[x-1]
     * @param y1 - points[x]
     * @param y2 - points[x+1]
     * @param y3 - points[x+2]
     * @param x - step between 0 and 1
     */
    public static double cubicHermite(double y0, double y1, double y2, double y3, double x)
    {
        double a = -0.5 * y0 + 1.5 * y1 - 1.5 * y2 + 0.5 * y3;
        double b = y0 - 2.5 * y1 + 2 * y2 - 0.5 * y3;
        double c = -0.5 * y0 + 0.5 * y2;

        /* In original article, the return was:
         *
         * ax^3 + bx^2 + cx + y1
         *
         * But expression below is simply a refactored version of the
         * expression above which is more readable. If you'll decompose return
         * you'll get the same formula above:
         *
         * ax^3 + bx^2 + cx + y1
         * (ax^2 + b*x + c) * x + y1
         * ((ax + b) * x + c) * x + y1
         *
         * That's it folks.
         */
        return ((a * x + b) * x + c) * x + y1;
    }

    /**
     * Cubic interpolation between y1 and y2. Taken from paul's website.
     *
     * @param y0 - points[x-1]
     * @param y1 - points[x]
     * @param y2 - points[x+1]
     * @param y3 - points[x+2]
     * @param x - step between 0 and 1
     */
    public static float cubic(float y0, float y1, float y2, float y3, float x)
    {
        float a = y3 - y2 - y0 + y1;
        float b = y0 - y1 - a;
        float c = y2 - y0;

        return ((a * x + b) * x + c) * x + y1;
    }
}