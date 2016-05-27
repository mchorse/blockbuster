package noname.blockbuster.test;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandRecord extends CommandBase
{
	public String getCommandName()
	{
		return "record";
	}

	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "Usage: /record <savefile>, eg: /mocap-rec forestrun";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayer player = getCommandSenderAsPlayer(sender);
		
		if (args.length < 1)
		{
			sender.addChatMessage(new TextComponentString(getCommandUsage(null)));
			return;
		}

		Recorder recorder = Mocap.recordThreads.get(player);
		String username = player.getDisplayName().getFormattedText();
		
		if (recorder != null)
		{
			recorder.recordThread.capture = false;
			Mocap.broadcastMessage("Stopped recording " + username + " to file " + recorder.fileName + ".mocap");
			Mocap.recordThreads.remove(player);
			return;
		}

		synchronized (Mocap.recordThreads)
		{
			for (Recorder ar : Mocap.recordThreads.values())
			{
				if (ar.fileName.equals(args[0].toLowerCase()))
				{
					Mocap.broadcastMessage(ar.fileName + ".mocap is already being recorded to?");
					return;
				}
			}
		}

		Mocap.broadcastMessage("Started recording " + username + " to file " + args[0] + ".mocap");
		Recorder newRecorder = new Recorder();
		Mocap.recordThreads.put(player, newRecorder);
			
		newRecorder.fileName = args[0].toLowerCase();
		newRecorder.recordThread = new RecordThread(player, args[0]);
	}
}
