package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.recording.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * Sub-command /action request
 *
 * This command is responsible for requesting record frames from the server
 */
public class SubCommandActionRequest extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "request";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.request";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        Utils.sendRecord(args[0], getCommandSenderAsPlayer(sender));
    }
}