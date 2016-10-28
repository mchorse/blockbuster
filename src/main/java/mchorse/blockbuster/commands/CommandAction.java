package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.action.SubCommandActionPlay;
import mchorse.blockbuster.commands.action.SubCommandActionRecord;
import mchorse.blockbuster.commands.action.SubCommandActionRequest;
import mchorse.blockbuster.commands.action.SubCommandActionStop;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
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
 * In 1.3.1, this command was refactored into {@link SubCommandBase} command.
 */
public class CommandAction extends SubCommandBase
{
    public CommandAction()
    {
        /* Register sub-commands in alphabetical order */
        this.add(new SubCommandActionPlay());
        this.add(new SubCommandActionRecord());
        this.add(new SubCommandActionRequest());
        this.add(new SubCommandActionStop());
    }

    @Override
    public String getCommandName()
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
     * 2. Name tag
     * 3. Model name
     * 4. Skin resource location
     * 5. Invincible flag (boolean)
     *
     * And of course, all of those arguments are optional (i.e. have default
     * values).
     */
    public static EntityActor actorFromArgs(String[] args, World world) throws CommandException
    {
        EntityActor actor = null;

        String name = args.length >= 2 ? args[1] : "";
        String model = args.length >= 3 ? args[2] : "";
        String skin = args.length >= 4 ? args[3] : "";
        boolean invincible = args.length >= 5 ? CommandBase.parseBoolean(args[4]) : false;

        actor = new EntityActor(world);
        actor.modify(model, RLUtils.fromString(skin, model), false, true);
        actor.setEntityInvulnerable(invincible);
        actor.setCustomNameTag(name);

        return actor;
    }
}