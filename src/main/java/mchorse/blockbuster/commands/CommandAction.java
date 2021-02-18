package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.action.SubCommandActionAppend;
import mchorse.blockbuster.commands.action.SubCommandActionClear;
import mchorse.blockbuster.commands.action.SubCommandActionPlay;
import mchorse.blockbuster.commands.action.SubCommandActionRecord;
import mchorse.blockbuster.commands.action.SubCommandActionRequest;
import mchorse.blockbuster.commands.action.SubCommandActionStop;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.world.World;

/**
 * Command /action
 *
 * This command is responsible for recording player actions or playbacking
 * already recorded player actions.
 *
 * This command is merged version of CommandPlay and CommandRecord (which both
 * were removed in 1.1). These commands were merged together, because they had
 * similar signature and work with player recordings.
 *
 * In 1.4, this command was refactored into {@link SubCommandBase} command.
 */
public class CommandAction extends SubCommandBase
{
    public CommandAction()
    {
        /* Register sub-commands in alphabetical order */
        this.add(new SubCommandActionAppend());
        this.add(new SubCommandActionClear());
        this.add(new SubCommandActionPlay());
        this.add(new SubCommandActionRecord());
        this.add(new SubCommandActionRequest());
        this.add(new SubCommandActionStop());
    }

    @Override
    public String getName()
    {
        return "action";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.action.help";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Create an actor from command line arguments (i.e. String array).
     * Description of every element in array:
     *
     * 1. Ignored (since it's filename)
     * 2. Invincible flag (boolean)
     * 3+. NBT data of the morph
     *
     * And of course, all of those arguments are optional (i.e. have default
     * values).
     */
    public static EntityActor actorFromArgs(String[] args, World world) throws CommandException
    {
        EntityActor actor;
        AbstractMorph morph = null;

        boolean invincible = args.length >= 2 && CommandBase.parseBoolean(args[1]);
        String model = args.length >= 3 ? String.join(" ",SubCommandBase.dropFirstArguments(args, 2)) : null;

        if (model != null)
        {
            try
            {
                morph = MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(model));
            }
            catch (Exception e)
            {}
        }

        actor = new EntityActor(world);
        actor.modify(morph, false, true);
        actor.setEntityInvulnerable(invincible);

        return actor;
    }
}