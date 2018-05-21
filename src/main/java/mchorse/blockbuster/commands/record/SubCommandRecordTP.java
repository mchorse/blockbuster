package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record tp
 * 
 * This command is responsible for teleporting the player to the given 
 * frame at tick in given player recording.
 */
public class SubCommandRecordTP extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public String getCommandName()
    {
        return "tp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.tp";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        int tick = args.length > 1 ? CommandBase.parseInt(args[1], 0) : 0;
        Record record = CommandRecord.getRecord(filename);

        if (tick < 0 || tick >= record.actions.size())
        {
            throw new CommandException("record.tick_out_range", tick);
        }

        EntityPlayer player = getCommandSenderAsPlayer(sender);
        Frame frame = record.frames.get(tick);

        player.setPositionAndRotation(frame.x, frame.x, frame.x, frame.yaw, frame.pitch);

        if (player.isCreative())
        {
            player.capabilities.isFlying = true;
        }
    }
}