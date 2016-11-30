package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * Camera's sub-command /camera start
 *
 * This sub-command is responsible for starting current camera profile.
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
    public void processCommand(ICommandSender sender, String[] args)
    {
        ClientProxy.profileRunner.start();
        L10n.info(sender, "profile.start");
    }
}
