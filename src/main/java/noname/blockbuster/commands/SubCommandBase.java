package noname.blockbuster.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
     * Automated way to output command's and sub-commands' usage messages.
     *
     */
    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String message = I18n.format(this.getHelp()) + "\n\n";

        for (CommandBase command : this.subcommands)
        {
            message += I18n.format(command.getCommandUsage(sender)).split("\n")[0] + "\n";
        }

        return message.trim();
    }

    /**
     * Execute the command
     *
     * This method basically delegates the execution to the mathced sub-command,
     * if the command was found, otherwise it shows usage message. */
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

    /**
     * Get completions for this command or its sub-commands.
     *
     * This method is responsible for giving completions of this command (names
     * of sub-commands) or completions of subcommand.
     */
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 0)
        {
            return super.getTabCompletionOptions(server, sender, args, pos);
        }

        if (args.length == 1)
        {
            List<String> options = new ArrayList<String>();

            for (CommandBase command : this.subcommands)
            {
                options.add(command.getCommandName());
            }

            return getListOfStringsMatchingLastWord(args, options);
        }

        for (CommandBase command : this.subcommands)
        {
            if (command.getCommandName().equals(args[0]))
            {
                return command.getTabCompletionOptions(server, sender, dropFirstArgument(args), pos);
            }
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}
