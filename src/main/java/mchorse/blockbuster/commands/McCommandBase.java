package mchorse.blockbuster.commands;

import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * McHorse's base command class
 *
 * This command class is responsible for catching {@link CommandException}s and
 * output them as my error styled messages sent via {@link L10n}. This class
 * is also frees the check for required arguments (which is very often
 * redundant if for args.length).
 */
public abstract class McCommandBase extends CommandBase
{
    /**
     * Get the count of arguments which are required
     */
    public int getRequiredArgs()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < this.getRequiredArgs())
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        try
        {
            this.executeCommand(server, sender, args);
        }
        catch (CommandException e)
        {
            if (e.getMessage().startsWith("commands."))
            {
                throw e;
            }

            L10n.error(sender, e.getMessage(), e.getErrorObjects());
        }
    }

    /**
     * Execute the command's task
     */
    public abstract void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
}