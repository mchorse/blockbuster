package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.client.model.parsing.ModelExporter;
import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.metamorph.commands.CommandMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

/**
 * Command /model export
 *
 * This command is responsible for converting (i.e. exporting) in-game Minecraft
 * models (ModelBase or his children) to JSON scheme that supports my custom
 * models.
 *
 * This is attempt number two, and it's a successful attempt!
 */
public class SubCommandModelExport extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "export";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.export";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}model {8}export{r} {7}<entity_name> [entity_tag]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        /* Gather needed elements for exporter class */
        String type = args[0];
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(type), sender.getEntityWorld());

        if (args.length > 1)
        {
            try
            {
                NBTTagCompound tag = new NBTTagCompound();

                entity.writeToNBT(tag);
                tag.merge(JsonToNBT.getTagFromJson(CommandMorph.mergeArgs(args, 1)));
                entity.readFromNBT(tag);
            }
            catch (Exception e)
            {
                throw new CommandException("metamorph.error.morph.nbt", e.getMessage());
            }
        }

        Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);

        if (render == null || !(render instanceof RenderLivingBase) || !(entity instanceof EntityLivingBase))
        {
            Blockbuster.l10n.error(sender, "model.export.wrong_type", type);
            return;
        }

        /* Export the model */
        ModelExporter exporter = new ModelExporter((EntityLivingBase) entity, (RenderLivingBase) render);

        String output = exporter.exportJSON(type);
        File exportFolder = new File(CommonProxy.configFile.getAbsolutePath() + "/export");

        exportFolder.mkdirs();

        /* Save exported model */
        try
        {
            File destination = new File(CommonProxy.configFile.getAbsolutePath() + "/export/" + type.replaceAll("[^\\w\\d_-]", "_") + ".json");
            PrintWriter writer = new PrintWriter(destination);

            writer.print(output);
            writer.close();

            ITextComponent file = new TextComponentString(destination.getName());
            file.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, destination.getAbsolutePath()));
            file.getStyle().setUnderlined(Boolean.valueOf(true));

            Blockbuster.l10n.success(sender, "model.export.saved", type, file);
        }
        catch (FileNotFoundException e)
        {
            Blockbuster.l10n.error(sender, "model.export.error_save");
        }
    }

    /**
     * Auto-complete entity type list
     *
     * Brutally ripped from {@link CommandSummon} class
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList()) : Collections.<String>emptyList();
    }
}
