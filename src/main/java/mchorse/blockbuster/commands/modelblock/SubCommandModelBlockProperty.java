package mchorse.blockbuster.commands.modelblock;

import com.google.common.collect.ImmutableList;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class SubCommandModelBlockProperty extends SubCommandModelBlockBase
{
	public static final List<String> PROPERTIES = ImmutableList.of("enabled");

	@Override
	public String getName()
	{
		return "property";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "blockbuster.commands.modelblock.property";
	}

	@Override
	public int getRequiredArgs()
	{
		return 5;
	}

	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		TileEntityModel model = this.getModelBlock(sender, args);
		String property = args[3];

		if (!PROPERTIES.contains(property))
		{
			throw new CommandException("modelblock.wrong_property", property);
		}

		if (property.equals("enabled"))
		{
			model.enabled = CommandBase.parseBoolean(args[4]);
		}

		model.markDirty();

		int x = model.getPos().getX();
		int y = model.getPos().getY();
		int z = model.getPos().getZ();

		PacketModifyModelBlock message = new PacketModifyModelBlock(model.getPos(), model);
		Dispatcher.DISPATCHER.get().sendToAllAround(message, new NetworkRegistry.TargetPoint(sender.getEntityWorld().provider.getDimension(), x, y, z, 64));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length == 4)
		{
			return getListOfStringsMatchingLastWord(args, PROPERTIES);
		}
		else if (args.length == 5)
		{
			String property = args[3];

			if (property.equals("enabled"))
			{
				return getListOfStringsMatchingLastWord(args, BOOLEANS);
			}
		}

		return super.getTabCompletions(server, sender, args, targetPos);
	}
}