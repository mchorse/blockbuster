package mchorse.blockbuster.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.client.gui.utils.TabCompleter;
import mchorse.blockbuster.client.gui.widgets.GuiCompleterViewer;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiCirculate;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.blockbuster.network.common.camera.PacketRequestCameraProfiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * GUI playback
 *
 * This GUI is responsible for attaching some properties to playback button's
 * ItemStack.
 */
public class GuiPlayback extends GuiScreen
{
    private String stringTitle = I18n.format("blockbuster.gui.playback.title");
    private String stringCameraMode = I18n.format("blockbuster.gui.playback.camera_mode");
    private String stringProfile = I18n.format("blockbuster.gui.playback.profile");

    private EntityPlayer player;

    private GuiTextField profileField;
    private GuiCirculate cameraMode;
    private GuiButton done;

    private GuiCompleterViewer profiles;
    private List<String> completions = new ArrayList<String>();
    private TabCompleter completer;

    public GuiPlayback(EntityPlayer player)
    {
        this.player = player;
    }

    /* Input */

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 15 && this.profileField.isFocused())
        {
            this.completer.complete();

            int size = this.completer.getCompletions().size();
            this.profiles.setHeight(size * 20);
            this.profiles.setHidden(size == 0);
        }
        else
        {
            this.completer.resetDidComplete();
            this.profiles.setHidden(true);
        }

        super.keyTyped(typedChar, keyCode);

        this.profileField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (!this.profiles.isInside(mouseX, mouseY))
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        this.profileField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /* Actions */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        int id = button.id;

        if (id == 1)
        {
            this.cameraMode.toggle();
            this.setValue(this.cameraMode.getValue());
        }
        else if (id == 2)
        {
            this.saveAndQuit();
        }
    }

    private void saveAndQuit()
    {
        Dispatcher.sendToServer(new PacketPlaybackButton(this.cameraMode.getValue(), this.profileField.getText()));

        this.mc.displayGuiScreen(null);
    }

    /* Child screens */

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        this.profiles.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.profiles.handleMouseInput();
    }

    /* GUI and drawing */

    @Override
    public void initGui()
    {
        int x = 20;
        int y = 45;
        int w = 120;

        this.profileField = new GuiTextField(0, this.fontRendererObj, x + 1, y + 1, w - 2, 18);
        this.cameraMode = new GuiCirculate(1, x, y + 40, w, 20);
        this.done = new GuiButton(2, x, this.height - 30, w, 20, I18n.format("blockbuster.gui.done"));

        this.buttonList.add(this.cameraMode);
        this.buttonList.add(this.done);

        this.cameraMode.addLabel(I18n.format("blockbuster.gui.playback.nothing"));
        this.cameraMode.addLabel(I18n.format("blockbuster.gui.playback.play"));
        this.cameraMode.addLabel(I18n.format("blockbuster.gui.playback.load_profile"));

        NBTTagCompound compound = this.player.getHeldItemMainhand().getTagCompound();

        if (compound.hasKey("CameraPlay"))
        {
            this.setValue(1);
        }
        else if (compound.hasKey("CameraProfile"))
        {
            this.setValue(2, compound.getString("CameraProfile"));
        }
        else
        {
            this.setValue(0);
        }

        this.completer = new TabCompleter(this.profileField);
        this.profiles = new GuiCompleterViewer(this.completer);
        this.profiles.updateRect(x, y + 19, w, 140);
        this.profiles.setHidden(true);

        if (this.completions.isEmpty())
        {
            this.requestCompletions();
        }
        else
        {
            this.completer.setAllCompletions(this.completions);
        }
    }

    private void requestCompletions()
    {
        Dispatcher.sendToServer(new PacketRequestCameraProfiles());
    }

    public void setCompletions(List<String> completions)
    {
        this.completions.addAll(completions);

        if (this.completer != null)
        {
            this.completer.setAllCompletions(completions);
        }
    }

    private void setValue(int value)
    {
        this.setValue(value, "");
    }

    private void setValue(int value, String profile)
    {
        this.cameraMode.setValue(value);
        this.profileField.setEnabled(value == 2);
        this.profileField.setText(value == 2 ? profile : "");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int x = 20;
        int y = 35;

        this.drawDefaultBackground();
        this.drawString(this.fontRendererObj, this.stringTitle, x, 15, 0xffffffff);
        this.drawString(this.fontRendererObj, this.stringProfile, x, y, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringCameraMode, x, y + 40, 0xffcccccc);
        this.profileField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.profiles.drawScreen(mouseX, mouseY, partialTicks);
    }
}
