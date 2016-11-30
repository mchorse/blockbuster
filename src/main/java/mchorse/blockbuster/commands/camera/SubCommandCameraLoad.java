package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.camera.PacketLoadCameraProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * Camera's sub-command /camera load
 *
 * This sub-command is responsible for loading camera profile. It doesn't
 * actually load the profile by itself, but sends the message to the server with
 * request to load specific camera profile to the client.
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
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        Dispatcher.sendToServer(new PacketLoadCameraProfile(args[0]));
    }
}