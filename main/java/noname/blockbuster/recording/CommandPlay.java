package noname.blockbuster.recording;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import noname.blockbuster.entity.ActorEntity;

public class CommandPlay extends CommandBase
{
    ArrayList<PlayThread> playThreads = new ArrayList();

    @Override
    public String getCommandName()
    {
        return "play";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "/play <replay> <entityname>";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(null)));
            return;
        }

        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/records/" + args[0]);

        if (!file.exists())
        {
            Mocap.broadcastMessage("Can't find " + args[0] + " replay file!");
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
                Mocap.broadcastMessage(args[0] + " isn't a record file (or is an old version?)");
                in.close();
                return;
            }

            in.readLong();

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
        entity.setCustomNameTag(args[1]);
        entity.setNoAI(true);
        world.spawnEntityInWorld(entity);

        Iterator<PlayThread> iterator = this.playThreads.iterator();

        while (iterator.hasNext())
        {
            PlayThread item = iterator.next();

            if (!item.thread.isAlive())
            {
                iterator.remove();
            }
        }

        this.playThreads.add(new PlayThread(entity, args[0], true));
    }
}
