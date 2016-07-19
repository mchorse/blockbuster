package noname.blockbuster.commands.camera;

import java.io.IOException;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.commands.CommandCamera;

/**
 * Camera's save subcommand
 *
 * This subcommand is responsible for save current camera profile.
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
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String filename = args[0];

        try
        {
            CameraUtils.writeCameraProfile(filename, CommandCamera.getProfile());
            sender.addChatMessage(new TextComponentString("Current camera profile was saved."));
        }
        catch (IOException e)
        {
            sender.addChatMessage(new TextComponentString("Current camera profile couldn't saved."));
            e.printStackTrace();
        }
    }
}