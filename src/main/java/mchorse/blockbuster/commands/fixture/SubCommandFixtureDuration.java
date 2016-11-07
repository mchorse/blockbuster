package mchorse.blockbuster.commands.fixture;

import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Camera's sub-command /camera duration
 *
 * This sub-command is responsible for setting duration for specified camera
 * fixture at given index.
 */
public class SubCommandFixtureDuration extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "duration";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.duration";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CameraProfile profile = CommandCamera.getProfile();

        if (args.length == 0)
        {
            L10n.info(sender, "camera.duration.profile", profile.getDuration());
            return;
        }

        int index = CommandBase.parseInt(args[0]);

        if (!profile.has(index))
        {
            L10n.error(sender, "profile.not_exists", index);
            return;
        }

        AbstractFixture fixture = profile.get(index);

        if (args.length == 1)
        {
            L10n.info(sender, "camera.duration.fixture", index, fixture.getDuration());
            return;
        }

        fixture.setDuration(CommandBase.parseLong(args[1]));
    }
}
