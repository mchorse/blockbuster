package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.camera.Angle;
import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.camera.Point;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * Camera's sub-command /camera goto
 *
 * Teleports player to specific camera fixture with specified progress.
 */
public class SubCommandCameraGoto extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "goto";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.goto";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        EntityPlayer player = (EntityPlayer) sender;
        CameraProfile profile = CommandCamera.getProfile();
        Position pos = new Position(player);

        int index = CommandBase.parseInt(args[0]);
        float progress = 0;

        if (args.length > 1)
        {
            progress = (float) CommandBase.parseDouble(args[1], 0, 1);
        }

        if (!profile.has(index))
        {
            throw new CommandException("blockbuster.error.profile.not_exists", index);
        }

        Point point = pos.point;
        Angle angle = pos.angle;

        profile.get(index).applyFixture(progress, 0, pos);
        player.setPositionAndRotation(point.x, point.y, point.z, angle.yaw, angle.pitch);
    }
}
