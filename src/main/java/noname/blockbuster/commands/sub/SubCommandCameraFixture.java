package noname.blockbuster.commands.sub;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.Position;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.camera.fixtures.PathFixture;
import noname.blockbuster.commands.CommandCamera;

/**
 * Camera's fixture subcommand
 *
 * This subcommand is responsible for managing camera profile's fixtures.
 * This subcommand has following subcommands:
 *
 * - [X] Add any type of fixtures based on player values
 * - [X] Manage PathFixture's paths
 *    - [X] Add point
 *    - [X] Remove point
 * - [X] Edit fixture's properties
 */
public class SubCommandCameraFixture extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "fixture";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String subcommand = args[0];
        EntityPlayer player = (EntityPlayer) sender;
        CameraProfile profile = CommandCamera.runner.getProfile();

        if (subcommand.equals("add"))
        {
            String[] input = SubCommandBase.dropFirstArgument(args);

            profile.add(AbstractFixture.fromCommand(input, player));
        }
        if (subcommand.equals("edit") && args.length >= 2)
        {
            int index = CommandBase.parseInt(args[1]);

            profile.get(index).edit(SubCommandBase.dropFirstArguments(args, 2), player);
        }
        else if (subcommand.equals("duration") && args.length >= 3)
        {
            int index = CommandBase.parseInt(args[1]);
            long duration = CommandBase.parseInt(args[2]);

            profile.get(index).setDuration(duration);
        }
        else if (subcommand.equals("add_sub") && args.length >= 2)
        {
            int index = CommandBase.parseInt(args[1]);

            AbstractFixture fixture = profile.get(index);

            if (!(fixture instanceof PathFixture))
            {
                return;
            }

            PathFixture path = (PathFixture) fixture;
            path.addPoint(new Position(player));
        }
        else if (subcommand.equals("remove_sub") && args.length >= 3)
        {
            int index = CommandBase.parseInt(args[1]);
            int pathIndex = CommandBase.parseInt(args[2]);

            AbstractFixture fixture = profile.get(index);

            if (!(fixture instanceof PathFixture))
            {
                return;
            }

            PathFixture path = (PathFixture) fixture;
            path.removePoint(pathIndex);
        }
        else
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }
}
