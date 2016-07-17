package noname.blockbuster.commands;

import net.minecraft.command.ICommandSender;
import noname.blockbuster.camera.ProfileRunner;
import noname.blockbuster.commands.sub.SubCommandBase;
import noname.blockbuster.commands.sub.SubCommandCameraFixture;
import noname.blockbuster.commands.sub.SubCommandCameraProfile;
import noname.blockbuster.commands.sub.SubCommandCameraStart;
import noname.blockbuster.commands.sub.SubCommandCameraStop;

/**
 * Camera command
 *
 * This `camera` command is an interface to work with camera, in general, but
 * specifically this commands provides sub commands for manipulating camera
 * fixtures and camera profiles.
 */
public class CommandCamera extends SubCommandBase
{
    public static final ProfileRunner runner = new ProfileRunner();

    {
        /**
         * Register camera's subcommands
         */
        this.subcommands.add(new SubCommandCameraStart());
        this.subcommands.add(new SubCommandCameraStop());
        this.subcommands.add(new SubCommandCameraProfile());
        this.subcommands.add(new SubCommandCameraFixture());
    }

    @Override
    public String getCommandName()
    {
        return "camera";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.camera";
    }
}