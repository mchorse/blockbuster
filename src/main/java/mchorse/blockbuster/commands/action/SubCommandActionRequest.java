package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.recording.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /action request
 *
 * This command is responsible for requesting record frames from the server
 */
public class SubCommandActionRequest extends CommandBase
{
    @Override
    public String getName()
    {
        return "request";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.request";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        Utils.sendRecord(args[0], getCommandSenderAsPlayer(sender));
    }
}