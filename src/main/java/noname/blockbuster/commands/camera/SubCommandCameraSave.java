package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.commands.CommandCamera;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraProfile;

/**
 * Camera's sub-command /camera save
 *
 * This sub-command is responsible for saving the camera profile to the disk.
 * As with /camera load sub-command, this sub-command also sends message to
 * the server with request to save profile that was sent.
 */
public class SubCommandCameraSave extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "save";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.save";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        Dispatcher.sendToServer(new PacketCameraProfile(args[0], CommandCamera.getProfile()));
    }
}