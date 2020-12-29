package mchorse.blockbuster.aperture.gui;

import mchorse.aperture.Aperture;
import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.camera.CameraProfile;
import mchorse.aperture.camera.destination.AbstractDestination;
import mchorse.aperture.camera.destination.ClientDestination;
import mchorse.aperture.client.gui.GuiProfilesManager;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.network.common.PacketRequestProfiles;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;
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
    private String stringScene = I18n.format("blockbuster.gui.playback.scene");

    private GuiCirculateElement cameraMode;
    private GuiButtonElement done;

    public GuiStringListElement scenes;
    public GuiListElement profiles;
    public Area frame = new Area();

    private SceneLocation location;
    private int mode = 0;
    private String profile = "";

    private boolean aperture;
    private int frameWidth = 150;

    public GuiPlayback()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.aperture = CameraHandler.isApertureLoaded();

        this.scenes = new GuiStringListElement(mc, (value) -> this.location = new SceneLocation(value.get(0)));
        this.scenes.background().flex().relative(this.frame).y(35).w(1F).h(1F, -65);

        this.done = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.done"), (b) -> this.saveAndQuit());
        this.done.flex().relative(this.frame).set(0, 0, 0, 20).y(1, -20).w(1F, 0);

        if (this.aperture)
        {
            this.frameWidth = 300;

            this.profiles = this.createListElement(mc);
            this.profiles.background().flex().set(153, 35, 0, 0).relative(this.frame).w(147).h(1, -105);

            this.scenes.flex().w(147);

            this.cameraMode = new GuiCirculateElement(mc, (b) -> this.setValue(this.cameraMode.getValue()));
            this.cameraMode.addLabel(IKey.lang("blockbuster.gui.playback.nothing"));
            this.cameraMode.addLabel(IKey.lang("blockbuster.gui.playback.play"));
            this.cameraMode.addLabel(IKey.lang("blockbuster.gui.playback.load_profile"));
            this.cameraMode.flex().relative(this.frame).set(153, 0, 0, 20).y(1, -50).w(147);

            this.root.add(this.profiles, this.cameraMode);
            this.fillData();
        }

        this.root.add(this.scenes, this.done);
    }

    /* Aperture specific methods */

    @Optional.Method(modid = Aperture.MOD_ID)
    private GuiListElement createListElement(Minecraft mc)
    {
        return new GuiProfilesManager.GuiCameraProfilesList(mc, null);
    }

    @Optional.Method(modid = Aperture.MOD_ID)
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

    @Optional.Method(modid = Aperture.MOD_ID)
    public void selectCurrent()
    {
        this.selectCurrent(this.profile);
    }

    @Optional.Method(modid = Aperture.MOD_ID)
    public void selectCurrent(String profile)
    {
        List<CameraProfile> list = (List<CameraProfile>) this.profiles.getList();

        for (int i = 0; i < list.size(); i ++)
        {
            if (list.get(i).getDestination().toResourceLocation().toString().equals(profile))
            {
                this.profiles.setIndex(i);

                break;
            }
        }
    }

    @Optional.Method(modid = Aperture.MOD_ID)
    private void sendPlaybackButton()
    {
        Dispatcher.sendToServer(new PacketPlaybackButton(this.location, this.cameraMode.getValue(), this.getSelected()));
    }

    @Optional.Method(modid = Aperture.MOD_ID)
    private String getSelected()
    {
        CameraProfile current = (CameraProfile) this.profiles.getCurrentFirst();

        if (current != null)
        {
            return current.getDestination().toResourceLocation().toString();
        }

        return "";
    }

    @Optional.Method(modid = Aperture.MOD_ID)
    public void addDestination(AbstractDestination destination)
    {
        this.profiles.add(new CameraProfile(destination));
    }

    /* Remaining methods */

    public GuiPlayback setLocation(SceneLocation location, List<String> scenes)
    {
        this.location = location;

        this.scenes.clear();
        this.scenes.add(scenes);
        this.scenes.sort();
        this.scenes.setCurrentScroll(location.getFilename());

        return this;
    }

    public GuiPlayback setMode(int mode, String profile)
    {
        this.mode = mode;
        this.profile = profile;

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

        if (this.aperture)
        {
            this.selectCurrent(profile);
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void saveAndQuit()
    {
        if (this.aperture)
        {
            this.sendPlaybackButton();
        }
        else
        {
            Dispatcher.sendToServer(new PacketPlaybackButton(this.location, 0, ""));
        }

        this.mc.displayGuiScreen(null);
    }

    @Override
    public void initGui()
    {
        this.frame.set(this.width / 2 - this.frameWidth / 2, 10, this.frameWidth, this.height - 20);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        boolean isCameraProfile = this.cameraMode.getValue() == 2 && this.aperture;

        GuiDraw.drawCustomBackground(0, 0, this.width, this.height);
        this.drawString(this.fontRenderer, this.stringTitle, this.frame.x, this.frame.y, 0xffffffff);

        if (this.cameraMode != null)
        {
            this.drawString(this.fontRenderer, this.stringCameraMode, this.cameraMode.area.x, this.cameraMode.area.y - 12, 0xffcccccc);
        }

        if (isCameraProfile)
        {
            this.drawString(this.fontRenderer, this.stringProfile, this.profiles.area.x, this.profiles.area.y - 12, 0xffcccccc);
        }

        this.drawString(this.fontRenderer, this.stringScene, this.scenes.area.x, this.scenes.area.y - 12, 0xffcccccc);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}