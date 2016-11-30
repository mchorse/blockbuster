package mchorse.blockbuster.commands.fixture;

import java.util.List;

import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Camera's sub-command /camera add
 *
 * This sub-command is responsible for adding a new camera fixture to current
 * camera profile. The camera fixture that is going to be added is depends
 * on the values passed by this command.
 */
public class SubCommandFixtureAdd extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "add";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.add";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        CommandCamera.getProfile().add(AbstractFixture.fromCommand(args, (EntityPlayer) sender));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsFromIterableMatchingLastWord(args, AbstractFixture.STRING_TO_TYPE.keySet());
        }

        return super.addTabCompletionOptions(sender, args);
    }
}
