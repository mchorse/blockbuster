package mchorse.blockbuster.commands.record;

import com.google.common.collect.ImmutableList;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Command /record apply
 * 
 * This command is responsible for applying one player recording to another
 * player recording file
 */
public class SubCommandRecordApply extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "apply";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.apply";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}apply{r} {7}<target> <source> <properties> [relative] [from] [to]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 3;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String target = args[0];
        String source = args[1];
        List<String> properties = new ArrayList<String>();

        if (args[2].contains(","))
        {
            properties.addAll(Arrays.asList(args[2].split(",")));
        }
        else
        {
            properties.add(args[2]);
        }

        Record targetRecord = CommandRecord.getRecord(target);
        Record sourceRecord = CommandRecord.getRecord(source);

        List<String> newProperties = new ArrayList<String>();

        for (String property : properties)
        {
            if (property.equalsIgnoreCase("head"))
            {
                newProperties.addAll(ImmutableList.of("yaw", "yaw_head", "pitch"));
            }
            else if (property.equalsIgnoreCase("position"))
            {
                newProperties.addAll(ImmutableList.of("x", "y", "z"));
            }
            else if (SubCommandRecordClean.PROPERTIES.contains(property))
            {
                newProperties.add(property);
            }
        }

        if (newProperties.isEmpty())
        {
            throw new CommandException("record.wrong_apply_property", String.join(", ", properties));
        }

        int start = 0;
        int end = Math.min(targetRecord.frames.size() - 1, sourceRecord.frames.size() - 1);
        boolean relative = args.length >= 4 && CommandBase.parseBoolean(args[3]);

        if (args.length >= 5)
        {
            start = CommandBase.parseInt(args[4], start, end);
        }

        if (args.length >= 6)
        {
            end = CommandBase.parseInt(args[5], start, end);
        }

        for (String property : newProperties)
        {
            this.apply(property, targetRecord, sourceRecord, relative, start, end);
        }

        try
        {
            RecordUtils.saveRecord(targetRecord);

            Blockbuster.l10n.success(sender, "record.apply", target, String.join(", ", newProperties), source, start, end);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Blockbuster.l10n.error(sender, "record.couldnt_save", args[1]);
        }
    }

    private void apply(String property, Record targetRecord, Record sourceRecord, boolean relative, int start, int end)
    {
        double firstOriginal = SubCommandRecordClean.get(property, sourceRecord.frames.get(start));

        for (int i = start; i <= end; i++)
        {
            double original = SubCommandRecordClean.get(property, sourceRecord.frames.get(i));
            double value = SubCommandRecordClean.get(property, targetRecord.frames.get(i));

            SubCommandRecordClean.set(property, targetRecord.frames.get(i), relative ? value + (original - firstOriginal) : original);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 3)
        {
            List<String> props = new ArrayList<>(SubCommandRecordClean.PROPERTIES);

            props.add("head");
            props.add("position");

            return getListOfStringsMatchingLastWord(args, props);
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}