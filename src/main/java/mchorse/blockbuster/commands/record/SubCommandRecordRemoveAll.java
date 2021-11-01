package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.client.ClientHandlerConfirm;
import mchorse.mclib.network.mclib.common.PacketConfirm;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class SubCommandRecordRemoveAll extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "remove_all";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.remove_all";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}remove_all{r} {7}<filename> [force]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        Record record = CommandRecord.getRecord(filename);
        boolean force = (args.length > 1) ? CommandBase.parseBoolean(args[1]) : false;
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        this.dispatchConfirm(player, force, filename, (value) ->
        {
            if (value)
            {
                record.actions.replaceAll((actions) ->
                {
                    return null;
                });
            }
        });
    }

    private void dispatchConfirm(EntityPlayerMP player, boolean force, String filename, Consumer<Boolean> callback)
    {
        if (force)
        {
            callback.accept(force);
        }
        else
        {
            Dispatcher.sendTo(new PacketConfirm(ClientHandlerConfirm.GUI.MCSCREEN, IKey.format("blockbuster.commands.record.remove_all_modal", filename),(value) ->
            {
                callback.accept(value);
            }), player);
        }
    }
}
