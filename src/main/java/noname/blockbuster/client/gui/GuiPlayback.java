package noname.blockbuster.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noname.blockbuster.client.gui.elements.GuiCirculate;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketPlayback;

public class GuiPlayback extends GuiScreen
{
    private String stringTitle = I18n.format("blockbuster.gui.playback.title");
    private String stringCameraMode = I18n.format("blockbuster.gui.playback.camera_mode");
    private String stringProfile = I18n.format("blockbuster.gui.playback.profile");

    private EntityPlayer player;

    private GuiTextField profileField;
    private GuiCirculate cameraMode;
    private GuiButton done;

    public GuiPlayback(EntityPlayer player)
    {
        this.player = player;
    }

    /* Input */

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        this.profileField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

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
        Dispatcher.sendToServer(new PacketPlayback(this.cameraMode.getValue(), this.profileField.getText()));

        this.mc.displayGuiScreen(null);
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
    }
}
