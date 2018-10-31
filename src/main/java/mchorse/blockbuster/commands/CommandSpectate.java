package mchorse.blockbuster.commands;

import java.util.List;

import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;

/**
 * Spectate entity command - /spectate &lt;player&gt; &lt;entity&gt;
 * 
 * This command allows to make given player a spectator of given entity. 
 * I don't know why it's useful, but I think this can be useful.
 */
public class CommandSpectate extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "spectate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.spectate.help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        EntityPlayerMP player = getPlayer(server, sender, args[0]);

        if (player == null)
        {
            L10n.error(sender, "commands.no_player", args[0]);

            return;
        }

        List<Entity> entities = EntitySelector.matchEntities(sender, args[1], Entity.class);

        if (entities.isEmpty())
        {
            L10n.error(sender, "commands.no_entity", args[1]);

            return;
        }

        if (!player.isSpectator())
        {
            player.setGameType(GameType.SPECTATOR);
        }

        for (Entity entity : entities)
        {
            if (entity != player)
            {
                player.setSpectatingEntity(entity);

                break;
            }
        }
    }

    /**
     * Provide completion for player usernames for first argument
     */
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}