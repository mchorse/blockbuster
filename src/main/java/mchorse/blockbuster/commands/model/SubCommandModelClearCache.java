package mchorse.blockbuster.commands.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

/**
 * Command /model clear
 *
 * This sub-command is responsible for clearing texture cache from the textures
 * which were fetched from blockbuster.actors domains, and were cached as
 * dynamic texture (purple checkered).
 */
public class SubCommandModelClearCache extends CommandBase
{
    public static Field TEXTURE_MAP;

    @Override
    public String getCommandName()
    {
        return "clear";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.clear";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        Map<ResourceLocation, ITextureObject> map = getTextures(manager);

        if (map != null)
        {
            Iterator<Map.Entry<ResourceLocation, ITextureObject>> it = map.entrySet().iterator();

            while (it.hasNext())
            {
                Map.Entry<ResourceLocation, ITextureObject> entry = it.next();

                if (entry.getKey().getResourceDomain().equals("blockbuster.actors"))
                {
                    TextureUtil.deleteTexture(entry.getValue().getGlTextureId());

                    it.remove();
                }
            }
        }
    }

    /**
     * Get texture map from texture manager using reflection API
     */
    @SuppressWarnings("unchecked")
    public static Map<ResourceLocation, ITextureObject> getTextures(TextureManager manager)
    {
        if (TEXTURE_MAP == null)
        {
            setupTextureMapField(manager);
        }

        try
        {
            return (Map<ResourceLocation, ITextureObject>) TEXTURE_MAP.get(manager);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Setup texture map field which is looked up using the reflection API
     */
    @SuppressWarnings("rawtypes")
    public static void setupTextureMapField(TextureManager manager)
    {
        /* Finding the field which has holds the texture cache */
        for (Field field : manager.getClass().getDeclaredFields())
        {
            if (Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }

            field.setAccessible(true);

            try
            {
                Object value = field.get(manager);

                if (value instanceof Map && ((Map) value).keySet().iterator().next() instanceof ResourceLocation)
                {
                    TEXTURE_MAP = field;

                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}