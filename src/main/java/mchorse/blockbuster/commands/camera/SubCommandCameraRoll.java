package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.camera.CameraControl;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Camera's sub-command /camera roll
 *
 * This command is responsible for setting and getting roll of this client.
 */
public class SubCommandCameraRoll extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "roll";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.roll";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CameraControl control = CommandCamera.getControl();

        if (args.length == 0)
        {
            L10n.sendClient(sender, "blockbuster.info.camera.roll", control.roll);
        }
        else
        {
            control.roll = (float) CommandBase.parseDouble(args[0]);
        }
    }
}