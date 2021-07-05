package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.mclib.client.gui.framework.elements.GuiConfirmationScreen;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketConfirm;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
        return "{l}{6}/{r}record {8}delete{r} {7}<filename>{r}";
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

        if (!OpHelper.isPlayerOp(player)) return;

        String filename = args[0];
        Dispatcher.sendTo(new PacketConfirm((test)->
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmationScreen(IKey.lang("blockbuster.commands.record.delete_modal"), (value) ->
            {
                Dispatcher.sendToServer(new PacketConfirm(value));
            }));
        },
        (value) ->
        {
            if(value)
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
        }), player);
    }
}
