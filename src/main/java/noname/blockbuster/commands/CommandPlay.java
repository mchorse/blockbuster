package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.recording.Mocap;

/**
 * Command play
 *
 * This command is complementary command of Command record. This command plays
 * acted scene with given file name of the record, new displayed name and of
 * course skin.
 *
 * Side note: you can use this command in command block.
 */
public class CommandPlay extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "play";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "blockbuster.commands.play";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        Mocap.startPlayback(args, sender.getEntityWorld(), true);
    }
}