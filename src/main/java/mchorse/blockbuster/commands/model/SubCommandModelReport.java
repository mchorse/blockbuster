package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderJSON;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderOBJ;
import mchorse.blockbuster.api.loaders.lazy.ModelLazyLoaderVOX;
import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

import java.io.File;

/**
 * /model report
 *
 * This command generates a report of all the files in config/blockbuster/models/
 * and copies it to copy-paste buffer.
 */
public class SubCommandModelReport extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "report";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.report";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}model {8}report{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        File models = new File(ClientProxy.configFile, "models");
        StringBuilder output = new StringBuilder();

        output.append("Models folder skins report:\n\n");

        this.processRecursively(output, models, models, "", "", false);

        GuiScreen.setClipboardString(output.toString().trim());

        Blockbuster.l10n.success(sender, "commands.model_report");
    }

    private void processRecursively(StringBuilder output, File root, File models, String prefix, String indent, boolean isModel)
    {
        if (!models.isDirectory())
        {
            return;
        }

        File[] files = models.listFiles();

        for (File file : files)
        {
            if (!file.isFile())
            {
                continue;
            }

            String name = file.getName();
            String aux = "";
            boolean obj = name.endsWith(".obj");
            boolean vox = name.endsWith(".vox");

            if (!isModel && (obj || name.equals("model.json") || vox))
            {
                IModelLazyLoader loader = Blockbuster.proxy.pack.models.get(prefix);

                if (loader instanceof ModelLazyLoaderOBJ && obj)
                {
                    isModel = true;
                    aux += ", loaded OBJ";
                }
                else if (loader instanceof ModelLazyLoaderVOX && vox)
                {
                    isModel = true;
                    aux += ", loaded VOX";
                }
                else if (loader != null && loader.getClass() == ModelLazyLoaderJSON.class)
                {
                    isModel = true;
                    aux += ", loaded JSON";
                }
            }

            if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif"))
            {
                ResourceLocation location = RLUtils.create("b.a:" + (prefix.isEmpty() ? "" : prefix + "/") + name);
                ITextureObject texture = Minecraft.getMinecraft().renderEngine.getTexture(location);

                if (texture == TextureUtil.MISSING_TEXTURE)
                {
                    aux += ", loaded but missing";
                }
                else if (texture != null)
                {
                    aux += ", loaded";
                }
            }

            output.append(indent);
            output.append(name);
            output.append(aux);
            output.append("\n");
        }

        for (File file : files)
        {
            if (!file.isDirectory())
            {
                continue;
            }

            output.append(indent);
            output.append(file.getName());
            output.append("/\n");
            this.processRecursively(output, root, file, prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName(), indent + "    ", isModel);
        }
    }
}