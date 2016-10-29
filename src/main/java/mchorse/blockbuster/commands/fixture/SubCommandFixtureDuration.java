package mchorse.blockbuster.commands.fixture;

import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

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
            long duration = profile.getDuration();

            L10n.sendClient(sender, "blockbuster.info.camera.duration.profile", duration);
            return;
        }

        int index = CommandBase.parseInt(args[0]);

        if (!profile.has(index))
        {
            L10n.sendColoredClient(sender, TextFormatting.DARK_RED, "blockbuster.error.profile.not_exists", index);
            return;
        }

        AbstractFixture fixture = profile.get(index);

        if (args.length == 1)
        {
            long duration = fixture.getDuration();

            L10n.sendClient(sender, "blockbuster.info.camera.duration.fixture", index, duration);
            return;
        }

        fixture.setDuration(CommandBase.parseLong(args[1]));
    }
}
