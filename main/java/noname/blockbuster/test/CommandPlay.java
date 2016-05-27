package noname.blockbuster.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import noname.blockbuster.entity.ActorEntity;

public class CommandPlay extends CommandBase
{
	ArrayList<PlayThread> playThreads = new ArrayList();

	public String getCommandName()
	{
		return "record-play";
	}

	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/record-play <replay> <skinname> <entityname>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 3)
		{
			sender.addChatMessage(new TextComponentString(getCommandUsage(null)));
			return;
		}
		
		File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/mocaps/" + args[0] + ".mocap");
		
		if (!file.exists())
		{
			Mocap.broadcastMessage("Can't find " + args[0] + ".mocap replay file!");
			return;
		}
		
		double x = 0.0D;
		double y = 0.0D;
		double z = 0.0D;
		
		try
		{
			RandomAccessFile in = new RandomAccessFile(file, "r");
			short magic = in.readShort();
			
			if (magic != Mocap.signature)
			{
				Mocap.broadcastMessage(args[0] + " isn't a .mocap file (or is an old version?)");
				in.close();
				return;
			}
			
			float yaw = in.readFloat();
			float pitch = in.readFloat();
			x = in.readDouble();
			y = in.readDouble();
			z = in.readDouble();
			
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		World world = sender.getEntityWorld();

		ActorEntity entity = new ActorEntity(world);
		entity.setPosition(x, y, z);
		entity.setCustomNameTag(args[2]);
		entity.setNoAI(true);
		world.spawnEntityInWorld(entity);

		Iterator<PlayThread> iterator = playThreads.iterator();
		
		while (iterator.hasNext())
		{
			PlayThread item = (PlayThread) iterator.next();
			
			if (!item.t.isAlive())
			{
				iterator.remove();
			}
		}
		
		playThreads.add(new PlayThread(entity, args[0]));
	}
}
