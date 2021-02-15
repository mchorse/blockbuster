package mchorse.blockbuster.recording.scene;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class SceneSender implements ICommandSender
{
    public Scene scene;

    public SceneSender(Scene scene)
    {
        this.scene = scene;
    }

    @Override
    public String getName() {
        return "SceneSender(" + this.scene.getId() + ")";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
    public void sendMessage(ITextComponent component)
    {}

    @Override
    public boolean canUseCommand(int permLevel, String commandName)
    {
        return true;
    }

    @Override
    public BlockPos getPosition()
    {
        return BlockPos.ORIGIN;
    }

    @Override
    public Vec3d getPositionVector()
    {
        return new Vec3d(this.getPosition());
    }

    @Override
    public World getEntityWorld()
    {
        return this.scene.getWorld();
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
    public void setCommandStat(CommandResultStats.Type type, int amount)
    {}

    @Override
    public MinecraftServer getServer()
    {
        return this.scene.getWorld().getMinecraftServer();
    }
}