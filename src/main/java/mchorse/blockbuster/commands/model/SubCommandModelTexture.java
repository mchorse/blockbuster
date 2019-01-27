package mchorse.blockbuster.commands.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.MipmapTexture;
import mchorse.blockbuster.utils.L10n;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * Command /model texture
 *
 * This sub-command is responsible for configuring texture's filtering option 
 * and also mipmapping it. It also allows to revert these effects.
 */
public class SubCommandModelTexture extends CommandBase
{

    @Override
    public String getName()
    {
        return "texture";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.texture";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        ResourceLocation texture = RLUtils.create(args[0]);
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(manager);
        ITextureObject tex = map.get(texture);

        if (tex == null)
        {
            /* Apparently it's not loaded yet, so I guess we need to inform the 
             * user */
            L10n.info(sender, "commands.texture_not_loaded", args[0]);

            return;
        }

        boolean mipmap = false;

        if (args.length >= 3)
        {
            mipmap = CommandBase.parseBoolean(args[2]);
            boolean mipmapped = tex instanceof MipmapTexture;

            /* Add or remove mipmap */
            if (mipmap && !mipmapped)
            {
                GlStateManager.deleteTexture(map.remove(texture).getGlTextureId());

                try
                {
                    /* Load texture manually */
                    tex = new MipmapTexture(texture);
                    tex.loadTexture(Minecraft.getMinecraft().getResourceManager());

                    map.put(texture, tex);
                    L10n.success(sender, "commands.mipmaped_texture", texture.toString());
                }
                catch (Exception e)
                {
                    System.err.println("An error occurred during loading manually a mipmap'd texture '" + texture + "'");
                    e.printStackTrace();
                }
            }
            else if (!mipmap && mipmapped)
            {
                GlStateManager.deleteTexture(map.remove(texture).getGlTextureId());
                L10n.success(sender, "commands.demipmaped_texture", texture.toString());
            }
            else
            {
                /* In third case, where texture's mipmap wasn't touch, we want 
                 * to make sure that correct mipmap value will be passed to 
                 * the texture filter configuration */
                mipmap = mipmapped;
            }
        }

        /* Setup the linear/nearest filter for the texture */
        if (args.length >= 2)
        {
            boolean linear = CommandBase.parseBoolean(args[1]);

            manager.bindTexture(texture);

            int mod = linear ? (mipmap ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR) : (mipmap ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
            int mag = linear ? GL11.GL_LINEAR : GL11.GL_NEAREST;

            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mod);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
            L10n.success(sender, "commands.texture_filter_" + (linear ? "linear" : "nearest"));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if (args.length == 1)
        {
            TextureManager manager = Minecraft.getMinecraft().renderEngine;
            Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(manager);
            List<String> keys = new ArrayList<String>();

            for (ResourceLocation key : map.keySet())
            {
                keys.add(key.toString());
            }

            return getListOfStringsMatchingLastWord(args, keys);
        }

        if (args.length == 2 || args.length == 3)
        {
            return getListOfStringsMatchingLastWord(args, new String[] {"true", "false"});
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}