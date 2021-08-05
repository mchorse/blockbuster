package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.mclib.client.gui.framework.elements.GuiConfirmationScreen;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.client.ClientHandlerConfirm;
import mchorse.mclib.network.mclib.common.PacketConfirm;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SubCommandRecordDelete extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "delete";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.delete";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}delete{r} {7}<filename> [force]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        if (!OpHelper.isPlayerOp(player))
        {
            throw new CommandException("record.delete_rights");
        }

        String filename = args[0];

        //throws exception if recording doesn't exist
        CommandRecord.getRecord(filename);

        boolean force = (args.length>1) ? CommandBase.parseBoolean(args[1]) : false;

        if (force)
        {
            this.deleteRecording(filename);
        }
        else
        {
            Dispatcher.sendTo(new PacketConfirm(ClientHandlerConfirm.GUI.MCSCREEN, IKey.format("blockbuster.commands.record.delete_modal", filename),
                    (value) ->
                    {
                        if(value)
                        {
                            this.deleteRecording(filename);
                        }
                    }), player);
        }
    }

    private void deleteRecording(String filename)
    {
        try
        {
            RecordUtils.replayFile(filename).delete();
            RecordUtils.unloadRecord(CommonProxy.manager.records.get(filename));
            CommonProxy.manager.records.remove(filename);
        }
        catch (NullPointerException e)
        {}
    }
}
