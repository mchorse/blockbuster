package mchorse.blockbuster.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

/**
 * Localization utils
 *
 * This class provides shortcuts for sending messages to players. Pretty tired
 * of typing a lot of characters with provided API.
 *
 * API should be clear, short and concise.
 */
@SuppressWarnings("deprecation")
public class L10n
{
    /**
     * Send a translated message to player
     */
    public static void send(ICommandSender sender, String key, Object... objects)
    {
        sender.sendMessage(new TextComponentTranslation(key, objects));
    }

    /**
     * Send a translated message to player
     */
    public static void sendColored(ICommandSender sender, TextFormatting color, String key, Object... objects)
    {
        ITextComponent text = new TextComponentTranslation(key, objects);
        text.getStyle().setColor(color);

        sender.sendMessage(text);
    }

    /**
     * Send error message to the sender
     */
    public static void error(ICommandSender sender, String key, Object... objects)
    {
        sendWithMarker(sender, "§4(§cX§4)§r ", "blockbuster.error." + key, objects);
    }

    /**
     * Send success message to the sender
     */
    public static void success(ICommandSender sender, String key, Object... objects)
    {
        sendWithMarker(sender, "§2(§aV§2)§r ", "blockbuster.success." + key, objects);
    }

    /**
     * Send informing message to the sender
     */
    public static void info(ICommandSender sender, String key, Object... objects)
    {
        sendWithMarker(sender, "§9(§bi§9)§r ", "blockbuster.info." + key, objects);
    }

    /**
     * Send a message with given marker
     */
    public static void sendWithMarker(ICommandSender sender, String marker, String key, Object... objects)
    {
        ITextComponent message = new TextComponentString(marker);
        ITextComponent string = new TextComponentTranslation(key, objects);

        string.getStyle().setColor(TextFormatting.GRAY);

        System.out.println(String.format(I18n.translateToLocal(key), objects));
        System.out.println(string.getFormattedText());

        message.appendSibling(string);
        sender.sendMessage(message);
    }
}