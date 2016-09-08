package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1 || args[0].isEmpty())
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        CameraProfile profile = CommandCamera.getProfile();

        profile.reset();
        profile.setFilename(args[0]);
        sender.addChatMessage(new TextComponentTranslation("blockbuster.profile.new", args[0]));
    }
}