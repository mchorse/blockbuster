package noname.blockbuster.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public abstract class SubCommandBase extends CommandBase
{
    /**
     * Sub commands array, initialize your sub commands here.
     */
    protected List<CommandBase> subcommands = new ArrayList<CommandBase>();

    /**
     * Drop only the first argument
     */
    public static String[] dropFirstArgument(String[] input)
    {
        return dropFirstArguments(input, 1);
    }

    /**
     * Totally not copied from CommandHandler.
     */
    public static String[] dropFirstArguments(String[] input, int amount)
    {
        String[] astring = new String[input.length - amount];
        System.arraycopy(input, amount, astring, 0, input.length - amount);

        return astring;
    }

    /**
     * Get help message language key
     */
    protected abstract String getHelp();

    /**
     * Automated way to output subcommand's usage
     *
     */
    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String message = I18n.format(this.getHelp()) + "\n\n";

        for (CommandBase command : this.subcommands)
        {
            String key = command instanceof SubCommandBase ? ((SubCommandBase) command).getHelp() : command.getCommandUsage(sender);

            message += I18n.format(key) + "\n";
        }

        return message.trim();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        for (CommandBase command : this.subcommands)
        {
            if (command.getCommandName().equals(args[0]))
            {
                command.execute(server, sender, dropFirstArgument(args));

                return;
            }
        }

        throw new WrongUsageException(this.getCommandUsage(sender));
    }
}
