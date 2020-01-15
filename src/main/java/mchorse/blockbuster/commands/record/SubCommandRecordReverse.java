package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * Command /record reverse
 *
 * This command is responsible for reversing all frames and actions in
 * the player recording
 */
public class SubCommandRecordReverse extends SubCommandRecordBase
{
	@Override
	public int getRequiredArgs()
	{
		return 1;
	}

	@Override
	public String getCommandName()
	{
		return "reverse";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "blockbuster.commands.record.reverse";
	}

	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		String filename = args[0];

		Record record = CommandRecord.getRecord(filename);

		record.reverse();
		record.dirty = true;

		Utils.unloadRecord(record);
		L10n.success(sender, "record.reverse", args[0]);
	}
}