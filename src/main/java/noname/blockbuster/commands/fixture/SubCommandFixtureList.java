package noname.blockbuster.commands.fixture;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.commands.CommandCamera;

public class SubCommandFixtureList extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "list";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.list";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String fixtures = "";

        for (AbstractFixture fixture : CommandCamera.getProfile().getAll())
        {
            fixtures += fixture + "\n";
        }

        if (fixtures.equals(""))
        {
            fixtures = I18n.format("blockbuster.commands.camera.fixture.empty");
        }

        sender.addChatMessage(new TextComponentString(fixtures.trim()));
    }
}
