package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.model.SubCommandModelClear;
import mchorse.blockbuster.commands.model.SubCommandModelClearStructures;
import mchorse.blockbuster.commands.model.SubCommandModelCombine;
import mchorse.blockbuster.commands.model.SubCommandModelConvertSkin;
import mchorse.blockbuster.commands.model.SubCommandModelExport;
import mchorse.blockbuster.commands.model.SubCommandModelExportObj;
import mchorse.blockbuster.commands.model.SubCommandModelReload;
import mchorse.blockbuster.commands.model.SubCommandModelReport;

/**
 * Command /model
 *
 * Another client-side command which is responsible for
 */
public class CommandModel extends SubCommandBase
{
    /**
     * Model command's constructor
     *
     * This method is responsible for attaching sub commands for this model
     */
    public CommandModel()
    {
        this.add(new SubCommandModelClear());
        this.add(new SubCommandModelClearStructures());
        this.add(new SubCommandModelCombine());
        this.add(new SubCommandModelConvertSkin());
        this.add(new SubCommandModelExport());
        this.add(new SubCommandModelExportObj());
        this.add(new SubCommandModelReload());
        this.add(new SubCommandModelReport());
    }

    @Override
    public String getName()
    {
        return "model";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.model.help";
    }
}