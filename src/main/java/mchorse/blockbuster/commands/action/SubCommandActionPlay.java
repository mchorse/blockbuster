package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.commands.CommandAction;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Mode;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.world.World;

/**
 * Sub-command /action play
 *
 * This sub-command is responsible for starting playback of ghost actor from
 * given attributes.
 */
public class SubCommandActionPlay extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "play";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.play";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        World world = sender.getEntityWorld();
        EntityActor actor = CommandAction.actorFromArgs(args, world);

        CommonProxy.manager.startPlayback(args[0], actor, Mode.BOTH, true, true);
        world.spawnEntityInWorld(actor);
    }
}