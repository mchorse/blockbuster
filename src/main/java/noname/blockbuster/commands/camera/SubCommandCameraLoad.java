package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.commands.CommandCamera;

/**
 * Camera's load subcommand
 *
 * This subcommand is responsible for load camera profile.
 */
public class SubCommandCameraLoad extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "load";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.load";
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
            CommandCamera.setProfile(CameraUtils.readCameraProfile(filename), getCommandSenderAsPlayer(sender));

            sender.addChatMessage(new TextComponentString("Current camera profile was loaded."));
        }
        catch (Exception e)
        {
            sender.addChatMessage(new TextComponentString("Current camera profile couldn't be loaded."));
            e.printStackTrace();
        }
    }
}