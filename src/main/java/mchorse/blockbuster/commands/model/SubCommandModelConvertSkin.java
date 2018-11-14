package mchorse.blockbuster.commands.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import mchorse.blockbuster.common.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * Command /model convert
 *
 * This command is responsible for converting 64x32 skins to 64x64 and 
 * vice versa. 
 */
public class SubCommandModelConvertSkin extends CommandBase
{
    /**
     * @link https://codereview.stackexchange.com/questions/172849/checking-if-a-number-is-power-of-2-or-not 
     */
    public static boolean isPowerOfTwo(int number)
    {
        return number > 0 && ((number & (number - 1)) == 0);
    }

    @Override
    public String getCommandName()
    {
        return "convert";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.export.convert";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String model = args[0];
        String skin = args[1];

        if (!(model.equals("steve") || model.equals("fred")))
        {
            throw new CommandException("blockbuster.error.commands.convert_model", model);
        }

        ResourceLocation location = new ResourceLocation("blockbuster.actors", model + "/" + skin);

        try
        {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(location);
            BufferedImage image = ImageIO.read(resource.getInputStream());
            int w = image.getWidth();
            int h = image.getHeight();

            if (!(isPowerOfTwo(w) && isPowerOfTwo(h) && (w == h || w == h * 2)))
            {
                throw new CommandException("blockbuster.error.commands.convert_skin_size", w, h);
            }

            BufferedImage target;

            /* Convert to 64x64 */
            if (model.equals("steve"))
            {
                target = new BufferedImage(w, h * 2, 2);

                Graphics graphics = target.getGraphics();
                float s = w / 64F;

                /* These coordinates were copied from 
                 * ImageBufferDownload class */
                graphics.drawImage(image, 0, 0, null);
                graphics.setColor(new Color(0, 0, 0, 0));
                graphics.fillRect(0, h / 2, w, h / 2);
                this.drawImage(graphics, target, 24, 48, 20, 52, 4, 16, 8, 20, s);
                this.drawImage(graphics, target, 28, 48, 24, 52, 8, 16, 12, 20, s);
                this.drawImage(graphics, target, 20, 52, 16, 64, 8, 20, 12, 32, s);
                this.drawImage(graphics, target, 24, 52, 20, 64, 4, 20, 8, 32, s);
                this.drawImage(graphics, target, 28, 52, 24, 64, 0, 20, 4, 32, s);
                this.drawImage(graphics, target, 32, 52, 28, 64, 12, 20, 16, 32, s);
                this.drawImage(graphics, target, 40, 48, 36, 52, 44, 16, 48, 20, s);
                this.drawImage(graphics, target, 44, 48, 40, 52, 48, 16, 52, 20, s);
                this.drawImage(graphics, target, 36, 52, 32, 64, 48, 20, 52, 32, s);
                this.drawImage(graphics, target, 40, 52, 36, 64, 44, 20, 48, 32, s);
                this.drawImage(graphics, target, 44, 52, 40, 64, 40, 20, 44, 32, s);
                this.drawImage(graphics, target, 48, 52, 44, 64, 52, 20, 56, 32, s);
            }
            /* Else, convert from 64x64 to 64x32 */
            else
            {
                target = new BufferedImage(w, h / 2, 2);

                Graphics graphics = target.getGraphics();
                graphics.drawImage(image, 0, 0, (ImageObserver) null);
            }

            /* Set target to opposite model */
            String targetModel = model.equals("steve") ? "fred" : "steve";
            ImageIO.write(target, "png", new File(ClientProxy.configFile, "models/" + targetModel + "/skins/" + skin + ".png"));

            target.flush();
            image.flush();
        }
        catch (CommandException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new CommandException("blockbuster.error.commands.convert_skin", model, skin, e.getMessage());
        }

        mchorse.blockbuster.utils.L10n.success(sender, "commands.convert_skin", model, skin);
    }

    /**
     * Draw parts of the image to another image using graphics and with 
     * a custom scale so it could support high resolution skins
     */
    private void drawImage(Graphics graphics, BufferedImage image, float a1, float a2, float b1, float b2, float c1, float c2, float d1, float d2, float s)
    {
        graphics.drawImage(image, (int) (a1 * s), (int) (a2 * s), (int) (b1 * s), (int) (b2 * s), (int) (c1 * s), (int) (c2 * s), (int) (d1 * s), (int) (d2 * s), null);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "steve", "fred");
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}