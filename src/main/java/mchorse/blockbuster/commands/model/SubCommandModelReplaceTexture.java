package mchorse.blockbuster.commands.model;

import java.util.HashMap;
import java.util.Map;

import mchorse.blockbuster.commands.McCommandBase;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

/**
 * Command /model replace_texture
 *
 * This sub-command is responsible for replacing and restoring textures in
 * Minecraft's texture map. Very useful if you want to change some mob's skin
 * without having to create and reload your own texture pack.
 */
public class SubCommandModelReplaceTexture extends McCommandBase
{
    /**
     * Map which is going to store original textures
     */
    public static final Map<ResourceLocation, ITextureObject> ORIGINAL = new HashMap<ResourceLocation, ITextureObject>();

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public String getName()
    {
        return "replace_texture";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.replace_texture";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(manager);

        if (map != null)
        {
            ResourceLocation target = new ResourceLocation(args[0]);

            /* Restore original */
            if (args.length < 2)
            {
                if (ORIGINAL.containsKey(target))
                {
                    map.put(target, ORIGINAL.remove(target));

                    L10n.info(sender, "commands.restored_texture", args[0]);
                }
                else
                {
                    L10n.error(sender, "commands.restore_texture", args[0]);
                }
            }
            /* Replace */
            else
            {
                ResourceLocation replace = new ResourceLocation(args[1]);

                boolean hasTarget = map.containsKey(target);
                boolean hasReplace = map.containsKey(replace);

                if (hasReplace && hasTarget)
                {
                    if (!ORIGINAL.containsKey(target))
                    {
                        ORIGINAL.put(target, map.get(target));
                    }

                    map.put(target, map.get(replace));

                    L10n.info(sender, "commands.replace_texture", args[0], args[1]);
                }
                else
                {
                    if (!hasTarget)
                    {
                        L10n.error(sender, "commands.replace_texture_one", args[0]);
                    }
                    else if (!hasReplace)
                    {
                        L10n.error(sender, "commands.replace_texture_one", args[1]);
                    }
                    else
                    {
                        L10n.error(sender, "commands.replace_texture", args[0], args[1]);
                    }
                }
            }
        }
    }
}