package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketLoadCameraProfile;

/**
 * Camera's load subcommand
 *
 * This subcommand is responsible for load camera profile.
 */
public class SubCommandCameraLoad extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "load";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.load";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        Dispatcher.getInstance().sendToServer(new PacketLoadCameraProfile(args[0]));
    }
}