package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.commands.CommandCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * Sub-command /camera default
 *
 * This sub-command resets camera's Field-Of-View and roll to default values
 * (70.0 degrees and 0.0 degress).
 */
public class SubCommandCameraDefault extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "default";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.default";
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_)
    {
        Minecraft.getMinecraft().gameSettings.fovSetting = 70.0F;
        CommandCamera.getControl().roll = 0.0F;
    }
}