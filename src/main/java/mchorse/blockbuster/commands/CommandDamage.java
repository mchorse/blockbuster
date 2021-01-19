package mchorse.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;

public class CommandDamage extends CommandBase
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

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

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
