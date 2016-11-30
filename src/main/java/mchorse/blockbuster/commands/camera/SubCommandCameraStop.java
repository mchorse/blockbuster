package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * Camera's sub-command /camera stop
 *
 * This sub-command is responsible for stopping current running camera profile.
 */
public class SubCommandCameraStop extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "stop";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.stop";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        ClientProxy.profileRunner.stop();
        L10n.info(sender, "profile.stop");
    }
}