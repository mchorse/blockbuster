package mchorse.blockbuster.commands;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import mchorse.blockbuster.recording.sounds.SoundEventListener;
import mchorse.blockbuster.recording.sounds.SoundSession;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;

public class CommandRecordSound extends CommandBase
{
    @Override
    public String getName()
    {
        return "record_sound";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record_sound";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player = CommandBase.getCommandSenderAsPlayer(sender);

        if (!SoundEventListener.INSTANCE.isRecording(player))
        {
            SoundEventListener.INSTANCE.startRecording(player);

            sender.sendMessage(new TextComponentString("Started recording sound events!"));
        }
        else
        {
            SoundSession session = SoundEventListener.INSTANCE.stopRecording(player);

            GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
            Gson gson = builder.create();
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);

            jsonWriter.setIndent("    ");
            gson.toJson(session.sounds, List.class, jsonWriter);

            String output = writer.toString();
            String filename = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".json";

            File target = new File(DimensionManager.getCurrentSaveRootDirectory(), "blockbuster/sounds/" + filename);

            target.getParentFile().mkdirs();

            try
            {
                Files.write(output, target, Charset.defaultCharset());

                sender.sendMessage(new TextComponentString("Successfully recorded sound events in file " + filename + "!"));
            }
            catch (IOException e)
            {
                sender.sendMessage(new TextComponentString("Sound events couldn't be recorded, because: " + e.getMessage()));

                e.printStackTrace();
            }
        }
    }
}