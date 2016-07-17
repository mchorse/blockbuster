package noname.blockbuster.commands.sub;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.camera.Position;
import noname.blockbuster.camera.fixtures.IdleFixture;
import noname.blockbuster.commands.CommandCamera;

/**
 * Camera's fixture subcommand
 *
 * This subcommand is responsible for managing camera profile's fixtures.
 * This subcommand has following subcommands:
 *
 * - [/] Add any type of fixtures based on player values
 * - [ ] Manage PathFixture's paths
 *    - [ ] Add point
 *    - [ ] Remove point
 * - [ ] Edit fixture's properties
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

        if (subcommand.equals("add") && args.length >= 2)
        {
            long duration = CommandBase.parseLong(args[1]);
            IdleFixture fixture = new IdleFixture(duration, new Position(player));

            CommandCamera.runner.getProfile().add(fixture);
        }
        else if (subcommand.equals("edit") && args.length >= 2)
        {
            int index = CommandBase.parseInt(args[1]);

            CommandCamera.runner.getProfile().get(index).edit(SubCommandBase.dropFirstArguments(args, 2), player);
        }
        else
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }
}
