package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraState;

/**
 * Camera's stop subcommand
 *
 * This subcommand is responsible for stopping current running camera profile.
 */
public class SubCommandCameraStart extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "start";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.start";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Dispatcher.getInstance().sendTo(new PacketCameraState(true), getCommandSenderAsPlayer(sender));
    }
}
