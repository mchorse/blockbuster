package mchorse.blockbuster.commands.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        Field textureMap = null;

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
                    textureMap = field;

                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /* Iterating through texture cache and removing all elements */
        if (textureMap != null)
        {
            try
            {
                Map<ResourceLocation, ITextureObject> map = (Map<ResourceLocation, ITextureObject>) textureMap.get(manager);
                Iterator<Map.Entry<ResourceLocation, ITextureObject>> it = map.entrySet().iterator();

                while (it.hasNext())
                {
                    Map.Entry<ResourceLocation, ITextureObject> entry = it.next();

                    if (entry.getKey().getResourceDomain().equals("blockbuster.actors") && entry.getValue() instanceof DynamicTexture)
                    {
                        it.remove();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}