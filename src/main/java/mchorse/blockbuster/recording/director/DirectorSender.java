package mchorse.blockbuster.recording.director;

import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Director block command sender 
 */
public class DirectorSender implements ICommandSender
{
    private Director director;

    public DirectorSender(Director director)
    {
        this.director = director;
    }

    @Override
    public String getName()
    {
        return "DirectorSender";
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentString(this.getName());
    }

    @Override
    public void addChatMessage(ITextComponent component)
    {}

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        return true;
    }

    @Override
    public BlockPos getPosition()
    {
        return this.director.getTile().getPos();
    }

    @Override
    public Vec3d getPositionVector()
    {
        return new Vec3d(this.getPosition());
    }

    @Override
    public World getEntityWorld()
    {
        return this.director.getTile().getWorld();
    }

    @Override
    public Entity getCommandSenderEntity()
    {
        return null;
    }

    @Override
    public boolean sendCommandFeedback()
    {
        return false;
    }

    @Override
    public void setCommandStat(Type type, int amount)
    {}

    @Override
    public MinecraftServer getServer()
    {
        return this.director.getTile().getWorld().getMinecraftServer();
    }
}