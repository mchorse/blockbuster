package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.camera.PacketCameraReset;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Camera's sub-command /camera clear
 *
 * This command is responsible for reseting currently selected profile,
 * basically wiping out all of the fixtures that were added.
 */
public class SubCommandCameraClear extends CommandBase
{
    @Override
    public String getName()
    {
        return "clear";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.clear";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CommandCamera.getProfile().reset();

        Dispatcher.sendToServer(new PacketCameraReset());
        L10n.success(sender, "profile.clear");
    }
}