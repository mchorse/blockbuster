package mchorse.blockbuster.commands.fixture;

import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * Camera's sub-command /camera move
 *
 * This sub-command is responsible for moving camera fixture at passed index
 * to another place in camera profile.
 */
public class SubCommandFixtureMove extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "move";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.move";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        CommandCamera.getProfile().move(CommandBase.parseInt(sender, args[0]), CommandBase.parseInt(sender, args[1]));
    }
}
