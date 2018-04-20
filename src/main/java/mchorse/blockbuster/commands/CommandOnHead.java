package mchorse.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandOnHead extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "on_head";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.on_head.help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ItemStack stack = player.getHeldItemMainhand();

        if (stack != null)
        {
            player.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack);
        }
    }
}