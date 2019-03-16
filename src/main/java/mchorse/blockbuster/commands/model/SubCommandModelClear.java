package mchorse.blockbuster.commands.model;

import java.util.Iterator;
import java.util.Map;

import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.mclib.utils.ReflectionUtils;
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
 * which were fetched from b.a domains, and were cached as dynamic 
 * texture (purple checkered).
 */
public class SubCommandModelClear extends CommandBase
{
    @Override
    public String getName()
    {
        return "clear";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.clear";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(manager);
        String prefix = args.length == 0 ? "" : args[0];

        if (map != null)
        {
            Iterator<Map.Entry<ResourceLocation, ITextureObject>> it = map.entrySet().iterator();

            while (it.hasNext())
            {
                Map.Entry<ResourceLocation, ITextureObject> entry = it.next();
                ResourceLocation key = entry.getKey();
                String domain = key.getResourceDomain();

                boolean bbDomain = domain.equals("b.a") || domain.equals("http") || domain.equals("https");

                if (bbDomain && key.getResourcePath().startsWith(prefix))
                {
                    TextureUtil.deleteTexture(entry.getValue().getGlTextureId());

                    if (!prefix.isEmpty())
                    {
                        ModelExtrudedLayer.clearByTexture(key);
                    }

                    it.remove();
                }
            }
        }

        if (prefix.isEmpty())
        {
            ModelExtrudedLayer.clear();
        }
    }
}