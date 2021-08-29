package mchorse.blockbuster.commands;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.commands.record.SubCommandRecordAdd;
import mchorse.blockbuster.commands.record.SubCommandRecordApply;
import mchorse.blockbuster.commands.record.SubCommandRecordCalculateBodyYaw;
import mchorse.blockbuster.commands.record.SubCommandRecordCamera;
import mchorse.blockbuster.commands.record.SubCommandRecordClean;
import mchorse.blockbuster.commands.record.SubCommandRecordCreate;
import mchorse.blockbuster.commands.record.SubCommandRecordCut;
import mchorse.blockbuster.commands.record.SubCommandRecordDelete;
import mchorse.blockbuster.commands.record.SubCommandRecordDupe;
import mchorse.blockbuster.commands.record.SubCommandRecordErase;
import mchorse.blockbuster.commands.record.SubCommandRecordFade;
import mchorse.blockbuster.commands.record.SubCommandRecordFill;
import mchorse.blockbuster.commands.record.SubCommandRecordFlip;
import mchorse.blockbuster.commands.record.SubCommandRecordGet;
import mchorse.blockbuster.commands.record.SubCommandRecordInfo;
import mchorse.blockbuster.commands.record.SubCommandRecordMerge;
import mchorse.blockbuster.commands.record.SubCommandRecordOrigin;
import mchorse.blockbuster.commands.record.SubCommandRecordProcess;
import mchorse.blockbuster.commands.record.SubCommandRecordProlong;
import mchorse.blockbuster.commands.record.SubCommandRecordRemove;
import mchorse.blockbuster.commands.record.SubCommandRecordRemoveAll;
import mchorse.blockbuster.commands.record.SubCommandRecordRemoveBodyYaw;
import mchorse.blockbuster.commands.record.SubCommandRecordRename;
import mchorse.blockbuster.commands.record.SubCommandRecordRestore;
import mchorse.blockbuster.commands.record.SubCommandRecordReverse;
import mchorse.blockbuster.commands.record.SubCommandRecordSearch;
import mchorse.blockbuster.commands.record.SubCommandRecordTP;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.L10n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.io.FileNotFoundException;

/**
 * Command /record
 *
 * This command provides an interface which allows the manipulation of player
 * recordings on the server.
 */
public class CommandRecord extends SubCommandBase
{
    public CommandRecord()
    {
        /* Register sub-commands */
        this.add(new SubCommandRecordAdd());
        this.add(new SubCommandRecordApply());
        this.add(new SubCommandRecordCalculateBodyYaw());

        if (CameraHandler.isApertureLoaded())
        {
            this.add(new SubCommandRecordCamera());
        }

        this.add(new SubCommandRecordClean());
        this.add(new SubCommandRecordCreate());
        this.add(new SubCommandRecordCut());
        this.add(new SubCommandRecordDupe());
        this.add(new SubCommandRecordErase());
        this.add(new SubCommandRecordFade());
        this.add(new SubCommandRecordFill());
        this.add(new SubCommandRecordFlip());
        this.add(new SubCommandRecordGet());
        this.add(new SubCommandRecordInfo());
        this.add(new SubCommandRecordMerge());
        this.add(new SubCommandRecordOrigin());
        this.add(new SubCommandRecordProcess());
        this.add(new SubCommandRecordProlong());
        this.add(new SubCommandRecordDelete());
        this.add(new SubCommandRecordRemove());
        this.add(new SubCommandRecordRemoveAll());
        this.add(new SubCommandRecordDelete());
        this.add(new SubCommandRecordRemoveBodyYaw());
        this.add(new SubCommandRecordRename());
        this.add(new SubCommandRecordRestore());
        this.add(new SubCommandRecordReverse());
        this.add(new SubCommandRecordSearch());
        this.add(new SubCommandRecordTP());
    }

    @Override
    public L10n getL10n()
    {
        return Blockbuster.l10n;
    }

    @Override
    public String getName()
    {
        return "record";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.help";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Get record by given filename
     *
     * This is a command-friendly method for retrieving a player recording. In
     * case of error, {@link CommandException} will be thrown.
     */
    public static Record getRecord(String filename) throws CommandException
    {
        try
        {
            return CommonProxy.manager.get(filename);
        }
        catch (FileNotFoundException e)
        {
            throw new CommandException("record.not_exist", filename);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new CommandException("recording.read", filename);
        }
    }
}