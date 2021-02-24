package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.blockbuster.commands.CommandAction;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Mode;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * Sub-command /action play
 *
 * This sub-command is responsible for starting playback of ghost actor from
 * given attributes.
 */
public class SubCommandActionPlay extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "play";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.play";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}action {8}play{r} {7}<filename> [invincibility] [morph_nbt]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        World world = sender.getEntityWorld();
        EntityActor actor = CommandAction.actorFromArgs(args, world);

        CommonProxy.manager.play(args[0], actor, Mode.BOTH, true);
        world.spawnEntity(actor);
    }
}