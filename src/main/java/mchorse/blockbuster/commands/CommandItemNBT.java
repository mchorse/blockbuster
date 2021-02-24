package mchorse.blockbuster.commands;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CommandItemNBT extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "item_nbt";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.item_nbt.help";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}item_nbt {7}<give_command>{r}";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();

        if (stack.isEmpty())
        {
            Blockbuster.l10n.error(sender, "commands.item_nbt_empty");

            return;
        }

        boolean command = CommandBase.parseBoolean(args[0]);

        String output = "{}";

        if (command)
        {
            NBTTagCompound tag = new NBTTagCompound();

            stack.writeToNBT(tag);
            output = "/give @p " + tag.getString("id") + " " + stack.getCount() + " " + stack.getItemDamage();

            if (stack.hasTagCompound())
            {
                output += " " + stack.getTagCompound().toString();
            }
        }
        else if (stack.hasTagCompound())
        {
            output = stack.getTagCompound().toString();
        }

        GuiScreen.setClipboardString(output);
        Blockbuster.l10n.success(sender, "commands.item_nbt");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, BOOLEANS);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}