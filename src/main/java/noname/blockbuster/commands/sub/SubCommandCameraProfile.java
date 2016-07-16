package noname.blockbuster.commands.sub;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.commands.CommandCamera;

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

        CameraProfile profile = CommandCamera.runner.getProfile();
        String subcommand = args[0];

        if (subcommand.equals("remove"))
        {
            profile.remove(CommandBase.parseInt(args[1]));
        }
        else if (subcommand.equals("list"))
        {
            String fixtures = "";

            for (AbstractFixture fixture : profile.getAll())
            {
                fixtures += fixture + "\n";
            }

            sender.addChatMessage(new TextComponentString(fixtures.trim()));
        }
        else if (args.length < 3 && subcommand.equals("move"))
        {
            profile.move(CommandBase.parseInt(args[1]), CommandBase.parseInt(args[2]));
        }
        else if (subcommand.equals("reset"))
        {
            profile.reset();
        }
        else if (subcommand.equals("new"))
        {
            CommandCamera.runner.setProfile(new CameraProfile());
        }
        else
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }
}
