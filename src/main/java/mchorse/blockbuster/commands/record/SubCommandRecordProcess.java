package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Variable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Command /record process
 *
 * This command is responsible for processing given property according to given
 * math expression
 */
public class SubCommandRecordProcess extends SubCommandRecordBase
{
    public MathBuilder builder;
    public Variable initial;
    public Variable value;
    public Variable tick;
    public Variable from;
    public Variable to;
    public Variable factor;

    public SubCommandRecordProcess()
    {
        this.builder = new MathBuilder();

        this.builder.register(this.initial = new Variable("initial", 0));
        this.builder.register(this.value = new Variable("value", 0));
        this.builder.register(this.tick = new Variable("tick", 0));
        this.builder.register(this.from = new Variable("from", 0));
        this.builder.register(this.to = new Variable("to", 0));
        this.builder.register(this.factor = new Variable("factor", 0));
}

    @Override
    public int getRequiredArgs()
    {
        return 5;
    }

    @Override
    public String getName()
    {
        return "process";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.process";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        String property = args[1];
        Record record = CommandRecord.getRecord(filename);

        if (!SubCommandRecordClean.PROPERTIES.contains(property))
        {
            throw new CommandException("record.wrong_clean_property", property);
        }

        int start = 0;
        int end = record.getLength() - 1;

        start = CommandBase.parseInt(args[2], start, end);
        end = CommandBase.parseInt(args[3], start, end);

        double initial = SubCommandRecordClean.get(property, record.frames.get(start));
        String expression = String.join(" ", SubCommandBase.dropFirstArguments(args, 4));
        IValue math;

        try
        {
            math = this.builder.parse(expression);
        }
        catch (Exception e)
        {
            throw new CommandException("record.invalid_math_expression", expression);
        }

        this.from.set(start);
        this.to.set(end);

        for (int i = start; i <= end; i++)
        {
            Frame frame = record.frames.get(i);

            this.initial.set(initial);
            this.value.set(SubCommandRecordClean.get(property, frame));
            this.tick.set(i);
            this.factor.set((i - start) / (double) (end - start));

            SubCommandRecordClean.set(property, frame, math.get());
        }

        try
        {
            RecordUtils.saveRecord(record);

            Blockbuster.l10n.success(sender, "record.process", filename, property, start, end);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Blockbuster.l10n.error(sender, "record.couldnt_save", args[1]);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, SubCommandRecordClean.PROPERTIES);
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}