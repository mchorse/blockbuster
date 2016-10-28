package mchorse.blockbuster.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

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
     * Send a translated message to player, from client (not necessarily, but
     * I'm unsure if it's allowed to use {@link I18n#format(String, Object...)}).
     */
    public static void sendClient(ICommandSender sender, String key, Object... objects)
    {
        sender.addChatMessage(new TextComponentString(I18n.format(key, objects)));
    }
}