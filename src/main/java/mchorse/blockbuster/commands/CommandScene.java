package mchorse.blockbuster.commands;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.recording.scene.Scene;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Command /scene
 *
 * This command is responsible for playing or stopping scenes.
 */
public class CommandScene extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "scene";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.scene";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}scene {8}<play|toggle|stop|loop>{r} {7}<name> [flag]{r}";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(null));
        }

        String action = args[0];
        String name = args[1];
        Scene scene = CommonProxy.scenes.get(name, sender.getEntityWorld());

        if (scene == null)
        {
            Blockbuster.l10n.error(sender, "scene.no_scene", name);
            return;
        }

        String play = "scene.play";
        String stop = "scene.stop";

        if (action.equals("play"))
        {
            if (scene.playing)
            {
                Blockbuster.l10n.error(sender, "scene.playing", name);
                return;
            }

            scene.startPlayback(0);
            Blockbuster.l10n.success(sender, play, name);
        }
        else if (action.equals("stop"))
        {
            if (!scene.playing)
            {
                Blockbuster.l10n.error(sender, "scene.stopped", name);
                return;
            }

            scene.stopPlayback(true);
            Blockbuster.l10n.success(sender, stop, name);
        }
        else if (action.equals("loop") && args.length >= 2)
        {
            scene.loops = CommandBase.parseBoolean(args[2]);

            try
            {
                CommonProxy.scenes.save(scene.getId(), scene);

                Blockbuster.l10n.info(sender, "scene." + (scene.loops ? "looped" : "unlooped"));
            }
            catch (Exception e)
            {}
        }
        else if (action.equals("toggle"))
        {
            boolean isPlaying = scene.togglePlayback();
            Blockbuster.l10n.success(sender, isPlaying ? play : stop, name);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "play", "stop", "toggle", "loop");
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}