package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Command action
 *
 * This class is responsible for executing commands.
 */
public class CommandAction extends Action
{
    /**
     * Command to be executed
     */
    public String command = "";

    public CommandAction()
    {}

    public CommandAction(String command)
    {
        this.command = command;
    }

    @Override
    public byte getType()
    {
        return Action.COMMAND;
    }

    @Override
    public void apply(EntityActor actor)
    {
        if (!this.command.isEmpty())
        {
            MinecraftServer server = actor.worldObj.getMinecraftServer();

            if (server != null)
            {
                ICommandManager manager = server.getCommandManager();

                manager.executeCommand(new CommandSender(actor), this.command);
            }
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.command = tag.getString("Command");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Command", this.command);
    }

    /**
     * Command action's command sender
     *
     * This dude is responsible for providing {@link ICommandSender} for the
     * {@link CommandHandler}.
     */
    public static class CommandSender implements ICommandSender
    {
        public EntityActor actor;

        public CommandSender(EntityActor actor)
        {
            this.actor = actor;
        }

        @Override
        public String getName()
        {
            return "CommandAction";
        }

        @Override
        public ITextComponent getDisplayName()
        {
            return new TextComponentString("CommandAction");
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
            return new BlockPos(this.actor);
        }

        @Override
        public Vec3d getPositionVector()
        {
            return new Vec3d(this.actor.posX, this.actor.posY, this.actor.posZ);
        }

        @Override
        public World getEntityWorld()
        {
            return this.actor.worldObj;
        }

        @Override
        public Entity getCommandSenderEntity()
        {
            return this.actor;
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
            return this.actor.worldObj.getMinecraftServer();
        }
    }
}