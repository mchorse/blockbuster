package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

/**
 * Camera's sub-command /camera save
 *
 * This sub-command is responsible for saving the camera profile to the disk.
 * As with /camera load sub-command, this sub-command also sends message to
 * the server with request to save profile that was sent.
 */
public class SubCommandCameraSave extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "save";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.save";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CameraProfile profile = CommandCamera.getProfile();
        String filename = args.length == 0 ? profile.getFilename() : args[0];

        if (filename.isEmpty())
        {
            L10n.sendColoredClient(sender, TextFormatting.DARK_RED, "blockbuster.error.profile.empty_filename");
            return;
        }

        profile.setFilename(filename);
        profile.save();
    }
}