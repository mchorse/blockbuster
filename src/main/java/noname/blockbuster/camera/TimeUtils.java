package noname.blockbuster.camera;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;

/**
 * Time utilities methods that helps dealing with time
 */
public class TimeUtils
{
    private static final DecimalFormat formatter = new DecimalFormat("0.###");
    private static final Pattern time = Pattern.compile("(\\d+(?:\\.\\d+)?)([hms])?");

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

    /**
     * Get long duration from given command line argument. This method parses
     * given string argument (which is suppose to be time), and then converts
     * it into the long duration (based on the prefix and the value).
     */
    public static long getDuration(String argument) throws CommandException
    {
        Matcher matcher = time.matcher(argument);

        if (matcher.find())
        {
            long factor = TimeUtils.toMillis(matcher.group(2));

            return CommandBase.parseLong(matcher.group(1), 0, 1000 * 1000) * factor;
        }
        else
        {
            throw new CommandException("blockbuster.duration.wrong", argument);
        }
    }
}