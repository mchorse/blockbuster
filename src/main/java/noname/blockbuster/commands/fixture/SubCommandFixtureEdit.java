package noname.blockbuster.commands.fixture;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.commands.CommandCamera;
import noname.blockbuster.commands.SubCommandBase;

/**
 * Camera's sub-command /camera edit
 *
 * This command is responsible for editing camera fixture's values passed from
 * this command in string array or directly from player's properties such as
 * position and rotation.
 *
 * It also outputs fixture's values if the values aren't specified.
 */
public class SubCommandFixtureEdit extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "edit";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.edit";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        AbstractFixture fixture = CommandCamera.getProfile().get(CommandBase.parseInt(args[0]));

        if (args.length < 2)
        {
            sender.addChatMessage(new TextComponentString(fixture.toString()));
        }
        else
        {
            fixture.edit(SubCommandBase.dropFirstArgument(args), (EntityPlayer) sender);
        }
    }
}
