package noname.blockbuster.commands.sub;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class SubCommandBase extends CommandBase
{
    /**
     * Sub commands array, initialize your sub commands here.
     */
    protected List<CommandBase> subcommands = new ArrayList<CommandBase>();

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        if (args[0].equals("help"))
        {
            sender.addChatMessage(new TextComponentTranslation(this.getHelp()));
            return;
        }

        for (CommandBase command : this.subcommands)
        {
            if (command.getCommandName().equals(args[0]))
            {
                command.execute(server, sender, this.dropFirstArgument(args));

                return;
            }
        }

        throw new WrongUsageException(this.getCommandUsage(sender));
    }

    /**
     * Totally not copied from CommandHandler.
     */
    private String[] dropFirstArgument(String[] input)
    {
        String[] astring = new String[input.length - 1];
        System.arraycopy(input, 1, astring, 0, input.length - 1);

        return astring;
    }

    protected abstract String getHelp();
}
