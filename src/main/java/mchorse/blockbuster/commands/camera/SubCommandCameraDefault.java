package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /camera default
 *
 * This sub-command resets camera's Field-Of-View and roll to default values
 * (70.0 degrees and 0.0 degress).
 */
public class SubCommandCameraDefault extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "default";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.default";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CommandCamera.getControl().resetRoll();
        CommandCamera.getControl().resetFOV();
    }
}