package mchorse.blockbuster.commands;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Command /scene
 *
 * This command is responsible for playing or stopping scenes.
 */
public class CommandScene extends CommandBase
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
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
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
            L10n.error(sender, "scene.no_scene", name);
            return;
        }

        String play = "scene.play";
        String stop = "scene.stop";

        if (action.equals("play"))
        {
            if (scene.playing)
            {
                L10n.error(sender, "scene.playing", name);
                return;
            }

            scene.startPlayback(0);
            L10n.success(sender, play, name);
        }
        else if (action.equals("stop"))
        {
            if (!scene.playing)
            {
                L10n.error(sender, "scene.stopped", name);
                return;
            }

            scene.stopPlayback();
            L10n.success(sender, stop, name);
        }
        else if (action.equals("loop") && args.length >= 2)
        {
            scene.loops = CommandBase.parseBoolean(args[2]);

            try
            {
                CommonProxy.scenes.save(scene.getId(), scene);

                L10n.info(sender, "scene." + (scene.loops ? "looped" : "unlooped"));
            }
            catch (Exception e)
            {}
        }
        else if (action.equals("toggle"))
        {
            boolean isPlaying = scene.togglePlayback();
            L10n.success(sender, isPlaying ? play : stop, name);
        }
    }

    /**
     * Get abstract director from block pos
     */
    protected TileEntityDirector getDirector(ICommandSender sender, BlockPos pos)
    {
        TileEntity entity = sender.getEntityWorld().getTileEntity(pos);

        if (entity instanceof TileEntityDirector)
        {
            return (TileEntityDirector) entity;
        }

        return null;
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