package mchorse.blockbuster.client.gui.framework.elements.list;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;

public class GuiStringSearchListElement extends GuiSearchListElement<String>
{
    public GuiStringSearchListElement(Minecraft mc, Consumer<String> callback)
    {
        super(mc, callback);
    }

    @Override
    protected GuiListElement<String> createList(Minecraft mc, Consumer<String> callback)
    {
        return new GuiStringListElement(mc, callback);
    }
}