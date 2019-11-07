package mchorse.blockbuster_pack.client.gui;

import java.util.Map;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.gui.utils.GuiTransformations;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPosePanel extends GuiMorphPanel<CustomMorph, GuiCustomMorph> implements ILimbSelector
{
    private GuiPoseTransformations transforms;
    private GuiStringListElement limbs;
    private GuiButtonElement<GuiButton> resetPose;

    private ModelPose pose;

    public GuiPosePanel(Minecraft mc, GuiCustomMorph editor)
    {
        super(mc, editor);

        this.transforms = new GuiPoseTransformations(mc);
        this.transforms.resizer().parent(this.area).set(0, 0, 190, 70).x(0.5F, -95).y(1, -75);

        this.limbs = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbs.resizer().parent(this.area).set(10, 50, 105, 90).h(1, -55);

        this.resetPose = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.morphs.reset"), (b) ->
        {
            this.editor.setPanel(this.editor.general);
            this.editor.morph.customPose = null;
            this.editor.updateModelRenderer();
        });

        this.resetPose.resizer().parent(this.area).set(10, 10, 105, 20);
        this.children.add(this.limbs, this.resetPose, this.transforms);
    }

    @Override
    public void setLimb(String limbName)
    {
        ModelLimb limb = this.editor.morph.model.limbs.get(limbName);

        this.editor.modelRenderer.limb = limb;
        this.limbs.setCurrent(limbName);
        this.setTransform(this.pose.limbs.get(limbName));
    }

    public void setTransform(ModelTransform trans)
    {
        this.transforms.set(trans);
    }

    @Override
    public void fillData(CustomMorph morph)
    {
        super.fillData(morph);

        this.limbs.clear();
        this.limbs.add(this.morph.model.limbs.keySet());
        this.limbs.sort();
    }

    @Override
    public void startEditing()
    {
        CustomMorph custom = this.morph;

        if (custom.customPose == null)
        {
            this.pose = custom.getPose(this.mc.thePlayer, 0).clone();
            custom.customPose = this.pose;
        }
        else
        {
            this.pose = custom.customPose;
            this.pose.fillInMissing(custom.getPose(this.mc.thePlayer, true, 0));
        }

        Map.Entry<String, ModelTransform> entry = this.pose.limbs.entrySet().iterator().next();

        this.setLimb(entry.getKey());
        this.editor.modelRenderer.pose = this.pose;
        this.limbs.setCurrent(entry.getKey());
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.limbs.area.x, this.limbs.area.y, this.limbs.area.getX(1), this.limbs.area.getY(1), 0x88000000);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.limbs"), this.limbs.area.x, this.limbs.area.y - 12, 0xffffff);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    public static class GuiPoseTransformations extends GuiTransformations
    {
        public ModelTransform trans;

        public GuiPoseTransformations(Minecraft mc)
        {
            super(mc);
        }

        public void set(ModelTransform trans)
        {
            this.trans = trans;

            if (trans != null)
            {
                this.fillT(trans.translate[0], trans.translate[1], trans.translate[2]);
                this.fillS(trans.scale[0], trans.scale[1], trans.scale[2]);
                this.fillR(trans.rotate[0], trans.rotate[1], trans.rotate[2]);
            }
        }

        @Override
        public void setT(float x, float y, float z)
        {
            this.trans.translate[0] = x;
            this.trans.translate[1] = y;
            this.trans.translate[2] = z;
        }

        @Override
        public void setS(float x, float y, float z)
        {
            this.trans.scale[0] = x;
            this.trans.scale[1] = y;
            this.trans.scale[2] = z;
        }

        @Override
        public void setR(float x, float y, float z)
        {
            this.trans.rotate[0] = x;
            this.trans.rotate[1] = y;
            this.trans.rotate[2] = z;
        }
    }
}