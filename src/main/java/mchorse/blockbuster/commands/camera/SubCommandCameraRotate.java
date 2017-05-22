package mchorse.blockbuster.commands.camera;

import com.google.common.primitives.Doubles;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /camera rotate
 *
 * This command allows player to rotate camera more precisely using absolute or
 * relative values.
 */
public class SubCommandCameraRotate extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "rotate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.rotate";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player = (EntityPlayer) sender;

        double x = args.length > 0 ? parseRelativeDouble(args[0], player.rotationYaw) : player.rotationYaw;
        double y = args.length > 1 ? parseRelativeDouble(args[1], player.rotationPitch) : player.rotationPitch;

        player.setPositionAndRotation(player.posX, player.posY, player.posZ, (float) x, (float) y);
    }

    /**
     * Parse relative double with given base number
     *
     * User may provide relative or absolute value. This way it's very easy to
     * pick up numbers.
     */
    public static double parseRelativeDouble(String input, double base) throws CommandException
    {
        String first = input.substring(0, 1);
        boolean plus = first.equals("+");
        boolean minus = first.equals("-");

        try
        {
            if (plus || minus)
            {
                input = input.substring(1);
            }

            double number = Double.parseDouble(input);

            if (!Doubles.isFinite(number))
            {
                throw new NumberInvalidException("commands.generic.num.invalid", input);
            }

            if (plus || minus)
            {
                number = plus ? base + number : (minus ? base - number : number);
            }

            return number;
        }
        catch (NumberFormatException var3)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", input);
        }
    }

    /**
     * Parse relative long with given base number
     *
     * User may provide relative or absolute value. This way it's somewhat
     * easier to pick up numbers.
     */
    public static long parseRelativeLong(String input, long base) throws CommandException
    {
        String first = input.substring(0, 1);
        boolean plus = first.equals("+");
        boolean minus = first.equals("-");

        try
        {
            if (plus || minus)
            {
                input = input.substring(1);
            }

            long number = Long.parseLong(input);

            if (plus || minus)
            {
                number = plus ? base + number : (minus ? base - number : number);
            }

            return number;
        }
        catch (NumberFormatException var3)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", input);
        }
    }
}