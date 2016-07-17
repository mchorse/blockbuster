package noname.blockbuster.commands.sub;

import net.minecraft.command.ICommandSender;
import noname.blockbuster.commands.sub.fixture.SubCommandFixtureAdd;
import noname.blockbuster.commands.sub.fixture.SubCommandFixtureDuration;
import noname.blockbuster.commands.sub.fixture.SubCommandFixtureEdit;
import noname.blockbuster.commands.sub.fixture.SubCommandFixturePath;

/**
 * Camera's fixture subcommand
 *
 * This subcommand is responsible for managing camera profile's fixtures.
 * This subcommand has following subcommands:
 *
 * - [X] Add any type of fixtures based on player values
 * - [X] Manage PathFixture's paths
 *    - [X] Add point
 *    - [X] Remove point
 * - [X] Edit fixture's properties
 */
public class SubCommandCameraFixture extends SubCommandBase
{
    {
        this.subcommands.add(new SubCommandFixtureAdd());
        this.subcommands.add(new SubCommandFixtureEdit());
        this.subcommands.add(new SubCommandFixtureDuration());
        this.subcommands.add(new SubCommandFixturePath());
    }

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
    protected String getHelp()
    {
        return "blockbuster.commands.camera.fixture";
    }
}
