package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.commands.CommandCamera;

/**
 * Camera's profile subcommand
 *
 * This subcommand is responsible for managing current camera profile.
 * This subcommand has following subcommands:
 *
 * - [X] Create new profiles
 * - [X] Select current profile
 * - [X] Move fixtures around in current profile
 * - [X] Display all fixtures in current profile
 * - [X] Remove fixtures
 */
public class SubCommandCameraProfile extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "profile";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.profile";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        CameraProfile profile = CommandCamera.getProfile();
        String subcommand = args[0];

        if (subcommand.equals("remove"))
        {
            profile.remove(CommandBase.parseInt(args[1]));
        }
        else if (subcommand.equals("move") && args.length >= 3)
        {
            profile.move(CommandBase.parseInt(args[1]), CommandBase.parseInt(args[2]));
        }
        else if (subcommand.equals("reset"))
        {
            profile.reset();
        }
        else if (subcommand.equals("new"))
        {
            CommandCamera.setProfile(new CameraProfile(), getCommandSenderAsPlayer(sender));
        }
        else
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        CommandCamera.updateProfile(getCommandSenderAsPlayer(sender));
    }
}
