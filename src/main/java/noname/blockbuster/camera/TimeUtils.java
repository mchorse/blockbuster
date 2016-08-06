package noname.blockbuster.camera;

import java.text.DecimalFormat;

/**
 * Time utilities methods that helps dealing with time
 */
public class TimeUtils
{
    private static final DecimalFormat formatter = new DecimalFormat("0.###");

    /**
     * Format milliseconds into human-like readable string
     */
    public static String formatMillis(long duration)
    {
        float output = duration;

        output /= 1000;

        if (output < 60) return formatter.format(output) + "s";

        output /= 60;

        if (output < 60) return formatter.format(output) + "m";

        output /= 60;

        return formatter.format(output) + "h";
    }

    /**
     * Get a factor for suffix
     */
    public static long toMillis(String suffix)
    {
        if (suffix.equals("s")) return 1000;
        if (suffix.equals("m")) return 1000 * 60;
        if (suffix.equals("h")) return 1000 * 60 * 60;

        return 1;
    }
}