package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.recording.Mocap;

/**
 * Command /action
 *
 * This command is responsible for recording player actions or playbacking
 * already recorded player actions.
 *
 * This command is merged version of CommandPlay and CommandRecord (which both
 * were removed). These commands were merged together, because they had similar
 * signature and work with player recordings.
 */
public class CommandAction extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "action";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        String action = args[0];

        if (action.equals("record"))
        {
            Mocap.startRecording(args[1], getCommandSenderAsPlayer(sender));
        }
        else if (action.equals("play"))
        {
            Mocap.startPlayback(SubCommandBase.dropFirstArgument(args), sender.getEntityWorld(), true);
        }
    }
}
