package mchorse.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;

public class CommandDamage extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "damage";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.damage.help";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}damage {7}<entity> <amount>{r}";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        float damage = (float) CommandBase.parseDouble(args[1]);
        Entity entity = getEntity(server, sender, args[0]);

        if (damage < 0 && entity instanceof EntityLivingBase)
        {
            EntityLivingBase target = (EntityLivingBase) entity;

            target.setHealth(target.getHealth() + Math.abs(damage));
        }
        else if (damage > 0)
        {
            entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, damage);
        }
    }
}
