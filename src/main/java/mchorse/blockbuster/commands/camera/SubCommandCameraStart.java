package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Camera's sub-command /camera start
 *
 * This sub-command is responsible for starting current camera profile.
 */
public class SubCommandCameraStart extends CommandBase
{
    @Override
    public String getName()
    {
        return "start";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.start";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ClientProxy.profileRunner.start();
        L10n.info(sender, "profile.start");
    }
}
