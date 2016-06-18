package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.recording.Mocap;

/**
 * Record command
 *
 * This command can be used to record yourself and then play the character with
 * `play` command. This is in case if you don't want to overcomplicate your
 * life with director block, redstone and crowd of actor entities.
 *
 * By the way, you can use this command with command block (you can make your
 * director block with command blocks, just like SethBling).
 */
public class CommandRecord extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "record";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "blockbuster.commands.record";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player = getCommandSenderAsPlayer(sender);

        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        Mocap.startRecording(args[0], player);
    }
}
