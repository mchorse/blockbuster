package mchorse.blockbuster.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * Localization utils
 *
 * This class provides shortcuts for sending messages to players. Pretty tired
 * of typing a lot of characters with provided API.
 *
 * API should be clear, short and concise.
 */
public class L10n
{
    /**
     * Send a translated message to player
     */
    public static void send(ICommandSender sender, String key, Object... objects)
    {
        sender.addChatMessage(new ChatComponentTranslation(key, objects));
    }

    /**
     * Send a translated message to player
     */
    public static void sendColored(ICommandSender sender, EnumChatFormatting color, String key, Object... objects)
    {
        IChatComponent text = new ChatComponentTranslation(key, objects);
        text.getChatStyle().setColor(color);

        sender.addChatMessage(text);
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
        IChatComponent message = new ChatComponentTranslation(marker);
        IChatComponent string = new ChatComponentTranslation(key, objects);

        string.getChatStyle().setColor(EnumChatFormatting.GRAY);

        message.appendSibling(string);
        sender.addChatMessage(message);
    }
}