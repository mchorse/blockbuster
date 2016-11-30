package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.camera.PacketCameraReset;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * Camera's sub-command /camera new
 *
 * This command is responsible for replacing current camera profile with a new
 * blank camera profile.
 */
public class SubCommandCameraNew extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "new";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.new";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1 || args[0].isEmpty())
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        CameraProfile profile = CommandCamera.getProfile();

        profile.reset();
        profile.setFilename(args[0]);
        Dispatcher.sendToServer(new PacketCameraReset());
        L10n.info(sender, "profile.new", args[0]);
    }
}