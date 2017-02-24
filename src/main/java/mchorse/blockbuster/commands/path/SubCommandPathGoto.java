package mchorse.blockbuster.commands.path;

import mchorse.blockbuster.camera.Angle;
import mchorse.blockbuster.camera.Point;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * Path's sub-command /camera path goto
 *
 * This sub-command is responsible for teleporting given player to a point at
 * given index in a path fixture. Very useful for readjusting the point in a
 * path fixture.
 */
public class SubCommandPathGoto extends CommandBase
{
    @Override
    public String getName()
    {
        return "goto";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.path.goto";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        EntityPlayer player = (EntityPlayer) sender;

        int index = CommandBase.parseInt(args[0]);
        int point = CommandBase.parseInt(args[1]);
        AbstractFixture fixture = CommandCamera.getProfile().get(index);

        if (!(fixture instanceof PathFixture))
        {
            L10n.error(sender, "profile.not_path", index);
            return;
        }

        PathFixture path = (PathFixture) fixture;

        if (!path.hasPoint(point))
        {
            L10n.error(sender, "profile.no_path_point", index, point);
            return;
        }

        Position position = path.getPoint(point);

        Point pos = position.point;
        Angle angle = position.angle;

        player.setPositionAndRotation(pos.x, pos.y, pos.z, angle.yaw, angle.pitch);
        player.setVelocity(0, 0, 0);

        CommandCamera.getControl().roll = angle.roll;
        Minecraft.getMinecraft().gameSettings.fovSetting = angle.fov;
    }
}