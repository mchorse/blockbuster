package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.commands.CommandCamera;

/**
 * Camera's sub-command /camera clear
 *
 * This command is responsible for reseting currently selected profile,
 * basically wiping out all of the fixtures that were added.
 */
public class SubCommandCameraClear extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "clear";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.clear";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CommandCamera.getProfile().reset();
    }
}
