package mchorse.blockbuster.commands.fixture;

import java.util.List;

import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
    public String getName()
    {
        return "add";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.add";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        CommandCamera.getProfile().add(AbstractFixture.fromCommand(args, (EntityPlayer) sender));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, AbstractFixture.STRING_TO_TYPE.keySet());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}
