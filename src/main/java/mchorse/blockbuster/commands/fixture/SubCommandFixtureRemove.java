package mchorse.blockbuster.commands.fixture;

import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

/**
 * Camera's sub-command /camera remove
 *
 * This sub-command is responsible for removing camera fixture from currently
 * loaded camera profile at specified index.
 */
public class SubCommandFixtureRemove extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "remove";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.remove";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        int index = CommandBase.parseInt(args[0]);

        if (!CommandCamera.getProfile().has(index))
        {
            L10n.sendColoredClient(sender, TextFormatting.DARK_RED, "blockbuster.error.profile.not_exists", index);
            return;
        }

        CommandCamera.getProfile().remove(index);
    }
}
