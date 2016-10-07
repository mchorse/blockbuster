package mchorse.blockbuster.commands.fixture;

import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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

            sender.addChatMessage(new TextComponentString(I18n.format("blockbuster.duration.profile", duration)));
            return;
        }

        int index = CommandBase.parseInt(args[0]);

        if (!profile.has(index))
        {
            throw new CommandException("blockbuster.profile.not_exists", index);
        }

        AbstractFixture fixture = profile.get(index);

        if (args.length == 1)
        {
            long duration = fixture.getDuration();

            sender.addChatMessage(new TextComponentString(I18n.format("blockbuster.duration.fixture", index, duration)));
            return;
        }

        fixture.setDuration(CommandBase.parseLong(args[1]));
    }
}
