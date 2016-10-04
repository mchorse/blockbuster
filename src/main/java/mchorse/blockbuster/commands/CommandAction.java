package mchorse.blockbuster.commands;

import java.util.List;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.recording.RecordManager;
import mchorse.blockbuster.recording.RecordPlayer.Mode;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Command /action
 *
 * This command is responsible for recording player actions or playbacking
 * already recorded player actions.
 *
 * This command is merged version of CommandPlay and CommandRecord (which both
 * were removed). These commands were merged together, because they had similar
 * signature and work with player recordings.
 */
public class CommandAction extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "action";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String action = args[0];
        RecordManager manager = CommonProxy.manager;

        if (action.equals("record") && args.length >= 2)
        {
            EntityPlayer player = getCommandSenderAsPlayer(sender);
            boolean recording = manager.startRecording(args[1], player, Mode.ACTIONS, true);

            if (recording && args.length >= 5)
            {
                BlockPos pos = CommandBase.parseBlockPos(sender, args, 2, false);
                TileEntity tile = sender.getEntityWorld().getTileEntity(pos);

                if (tile instanceof TileEntityDirector)
                {
                    TileEntityDirector director = (TileEntityDirector) tile;

                    director.applyReplay(director.byFile(args[1]), player);
                    director.startPlayback(args[1]);
                }
            }
        }
        else if (action.equals("play") && args.length >= 2)
        {
            World world = sender.getEntityWorld();
            EntityActor actor = actorFromArgs(SubCommandBase.dropFirstArgument(args), world);

            manager.startPlayback(args[1], actor, Mode.BOTH, true, true);

            world.spawnEntityInWorld(actor);
        }
        else if (action.equals("stop"))
        {
            manager.stopRecording(getCommandSenderAsPlayer(sender), true);
        }
        else
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "record", "play", "stop");
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    /**
     * Create an actor from command line arguments (i.e. String array)
     */
    public static EntityActor actorFromArgs(String[] args, World world)
    {
        EntityActor actor = null;

        String name = args.length >= 2 ? args[1] : "";
        String model = args.length >= 4 ? args[3] : "";
        String skin = args.length >= 3 ? args[2] : "";
        boolean invincible = args.length >= 5 && args[4].equals("1");

        actor = new EntityActor(world);
        actor.modify(model, skin, false, true);
        actor.setEntityInvulnerable(invincible);
        actor.setCustomNameTag(name);

        return actor;
    }
}
