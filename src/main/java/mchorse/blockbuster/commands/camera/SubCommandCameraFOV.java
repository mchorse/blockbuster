package mchorse.blockbuster.commands.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Camera's sub-command /camera fov
 *
 * This command is responsible for setting and getting fov of this client.
 */
public class SubCommandCameraFOV extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "fov";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fov";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (args.length == 0)
        {
            sender.addChatMessage(new TextComponentString(I18n.format("blockbuster.camera.fov", mc.gameSettings.fovSetting)));
        }
        else
        {
            mc.gameSettings.fovSetting = (float) CommandBase.parseDouble(args[0]);
        }
    }
}