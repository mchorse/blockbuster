package mchorse.blockbuster.aperture.gui;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.camera.CameraProfile;
import mchorse.aperture.camera.destination.AbstractDestination;
import mchorse.aperture.camera.destination.ClientDestination;
import mchorse.aperture.client.gui.GuiProfilesManager;
import mchorse.blockbuster.aperture.network.common.PacketRequestProfiles;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * GUI playback
 *
 * This GUI is responsible for attaching some properties to playback button's
 * ItemStack.
 */
@SideOnly(Side.CLIENT)
public class GuiPlayback extends GuiBase
{
    private String stringTitle = I18n.format("blockbuster.gui.playback.title");
    private String stringCameraMode = I18n.format("blockbuster.gui.playback.camera_mode");
    private String stringProfile = I18n.format("blockbuster.gui.playback.profile");

    private GuiCirculateElement cameraMode;
    private GuiButtonElement done;

    public GuiProfilesManager.GuiCameraProfilesList profiles;
    public Area frame = new Area();

    private String profile;
    private SceneLocation location;

    public GuiPlayback()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.done = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.done"), (b) -> this.saveAndQuit());
        this.done.flex().relative(this.frame).set(0, 0, 0, 20).y(1, -20).w(1, 0);

        this.profiles = this.createListElement(mc);
        this.profiles.background();
        this.profiles.flex().set(0, 35, 0, 0).relative(this.frame).w(1, 0).h(1, -100);

        this.cameraMode = new GuiCirculateElement(mc, (b) -> this.setValue(this.cameraMode.getValue()));
        this.cameraMode.addLabel(IKey.lang("blockbuster.gui.playback.nothing"));
        this.cameraMode.addLabel(IKey.lang("blockbuster.gui.playback.play"));
        this.cameraMode.addLabel(IKey.lang("blockbuster.gui.playback.load_profile"));
        this.cameraMode.flex().relative(this.frame).set(0, 0, 0, 20).y(1, -45).w(1, 0);

        this.root.add(this.profiles, this.cameraMode);
        this.fillData();

        this.root.add(this.done);
    }

    /* Aperture specific methods */

    private GuiProfilesManager.GuiCameraProfilesList createListElement(Minecraft mc)
    {
        return new GuiProfilesManager.GuiCameraProfilesList(mc, null);
    }

    private void fillData()
    {
        /* Fill data */
        for (String filename : CameraAPI.getClientProfiles())
        {
            this.addDestination(new ClientDestination(filename));
        }

        this.profiles.sort();

        if (ClientProxy.server)
        {
            Dispatcher.sendToServer(new PacketRequestProfiles());
        }

        /* Fill the camera mode button */
        NBTTagCompound compound = Minecraft.getMinecraft().player.getHeldItemMainhand().getTagCompound();

        if (compound != null)
        {
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
        else
        {
            this.setValue(0);
        }
    }

    public void selectCurrent()
    {
        this.selectCurrent(this.profile);
    }

    public void selectCurrent(String profile)
    {
        List<CameraProfile> list = this.profiles.getList();

        for (int i = 0; i < list.size(); i ++)
        {
            if (list.get(i).getDestination().toResourceLocation().toString().equals(profile))
            {
                this.profiles.setIndex(i);

                break;
            }
        }
    }

    private void sendPlaybackButton()
    {
        Dispatcher.sendToServer(new PacketPlaybackButton(this.location, this.cameraMode.getValue(), this.getSelected()));
    }

    private String getSelected()
    {
        CameraProfile current = this.profiles.getCurrentFirst();

        if (current != null)
        {
            return current.getDestination().toResourceLocation().toString();
        }

        return "";
    }

    public void addDestination(AbstractDestination destination)
    {
        this.profiles.add(new CameraProfile(destination));
    }

    /* Remaining methods */

    public GuiPlayback setLocation(SceneLocation location)
    {
        this.location = location;

        return this;
    }

    public void setValue(int value)
    {
        this.cameraMode.setValue(value);
        this.profiles.setVisible(value == 2);
    }

    public void setValue(int value, String profile)
    {
        this.profile = profile;
        this.setValue(value);

        this.selectCurrent(profile);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void saveAndQuit()
    {
        this.sendPlaybackButton();
        this.mc.displayGuiScreen(null);
    }

    @Override
    public void initGui()
    {
        this.frame.set(this.width / 2 - 75, 10, 150, this.height - 20);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        boolean isCameraProfile = this.cameraMode.getValue() == 2;

        GuiDraw.drawCustomBackground(0, 0, this.width, this.height);
        this.drawString(this.fontRenderer, this.stringTitle, this.frame.x, this.frame.y, 0xffffffff);
        this.drawString(this.fontRenderer, this.stringCameraMode, this.frame.x, this.cameraMode.area.y - 12, 0xffcccccc);

        if (isCameraProfile)
        {
            this.drawString(this.fontRenderer, this.stringProfile, this.frame.x, this.profiles.area.y - 12, 0xffcccccc);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}