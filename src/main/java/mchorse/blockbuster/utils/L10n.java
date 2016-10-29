package mchorse.blockbuster.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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
        sender.addChatMessage(new TextComponentTranslation(key, objects));
    }

    /**
     * Send a translated message to player
     */
    public static void sendColored(ICommandSender sender, TextFormatting color, String key, Object... objects)
    {
        ITextComponent text = new TextComponentTranslation(key, objects);
        text.getStyle().setColor(color);

        sender.addChatMessage(text);
    }

    /**
     * Send a translated message to player, from client (not necessarily, but
     * I'm unsure if it's allowed to use {@link I18n#format(String, Object...)}).
     */
    public static void sendClient(ICommandSender sender, String key, Object... objects)
    {
        sender.addChatMessage(new TextComponentString(I18n.format(key, objects)));
    }

    /**
     * Send a translated message to player
     */
    public static void sendColoredClient(ICommandSender sender, TextFormatting color, String key, Object... objects)
    {
        ITextComponent text = new TextComponentString(I18n.format(key, objects));
        text.getStyle().setColor(color);

        sender.addChatMessage(text);
    }

    /**
     * Wrap given arguments into {@link TextComponentString} and give them
     * desired color formatting.
     */
    public static ITextComponent[] wrapArguments(TextFormatting color, String... strings)
    {
        ITextComponent[] text = new ITextComponent[strings.length];

        for (int i = 0; i < strings.length; i++)
        {
            text[i] = new TextComponentString(strings[i]);
            text[i].getStyle().setColor(color);
        }

        return text;
    }
}