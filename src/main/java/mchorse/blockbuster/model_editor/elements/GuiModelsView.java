package mchorse.blockbuster.model_editor.elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.model_editor.modal.GuiScrollView;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GuiModelsView extends GuiScrollView
{
    public List<ModelCell> models = new ArrayList<ModelCell>();
    public ModelCell selected;
    public EntityLivingBase dummy;

    public GuiModelsView(GuiScreen parent)
    {
        super(parent);

        for (Map.Entry<String, ModelCustom> model : ModelCustom.MODELS.entrySet())
        {
            ModelCell cell = new ModelCell(model.getValue(), model.getKey());

            /* Steve is always selected by default */
            if (cell.key.equals("blockbuster.steve"))
            {
                this.selected = cell;
            }

            this.models.add(cell);
        }

        this.dummy = new DummyEntity(null);
    }

    @Override
    public void initiate()
    {
        int index = this.models.indexOf(this.selected);

        this.scrollHeight = MathHelper.ceil(this.models.size() / 3.0F) * (this.w / 3);
        this.scrollTo(index == -1 ? 0 : index / 3 * this.w / 3);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.dragging)
        {
            return;
        }

        int x = mouseX - this.x;
        int y = mouseY - this.y + this.scrollY;

        int width = this.w / 3;
        int index = x / width + y / width * 3;

        if (index >= 0 && index < this.models.size())
        {
            this.selected = this.models.get(index);
        }
    }

    @Override
    protected void drawBackground()
    {}

    @Override
    protected void drawView(int mouseX, int mouseY, float partialTicks)
    {
        int i = 0;
        int w = this.w / 3;

        for (ModelCell model : this.models)
        {
            int x = this.x + (i % 3) * w;
            int y = this.y + (i / 3) * w;

            if (model.texture != null)
            {
                this.mc.renderEngine.bindTexture(model.texture);
                model.model.pose = model.model.model.getPose("standing");

                this.drawModel(model.model, this.dummy, x + w / 2, y + (int) (w * 0.8F), w / 2.5F);
            }

            if (this.selected == model)
            {
                this.renderSelected(x, y, w, w);
            }

            i++;
        }
    }

    /**
     * Render a grey outline around the given area.
     *
     * Basically, this method renders selection.
     */
    private void renderSelected(int x, int y, int width, int height)
    {
        int color = 0xffcccccc;

        this.drawHorizontalLine(x, x + width - 1, y, color);
        this.drawHorizontalLine(x, x + width - 1, y + height - 1, color);

        this.drawVerticalLine(x, y, y + height - 1, color);
        this.drawVerticalLine(x + width - 1, y, y + height - 1, color);
    }

    /**
     * Draw a {@link ModelBase} without using the {@link RenderManager} (which
     * adds a lot of useless transformations and stuff to the screen rendering).
     */
    public void drawModel(ModelBase model, EntityLivingBase entity, int x, int y, float scale)
    {
        float factor = 0.0625F;

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        GlStateManager.enableAlpha();

        model.setLivingAnimations(entity, 0, 0, 0);
        model.setRotationAngles(0, 0, 0, 0, 0, factor, entity);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        model.render(entity, 0, 0, 0, 0, 0, factor);

        GlStateManager.disableDepth();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static class ModelCell
    {
        public ModelCustom model;
        public ResourceLocation texture;
        public String key;

        public ModelCell(ModelCustom model, String key)
        {
            this.model = model;
            this.key = key;

            if (model.model.defaultTexture != null)
            {
                this.texture = model.model.defaultTexture;
            }
            else
            {
                int index = key.indexOf(".");

                String name = index == -1 ? key : key.substring(index + 1);
                Map<String, File> skins = Blockbuster.proxy.models.pack.skins.get(name);

                if (skins != null && !skins.isEmpty())
                {
                    this.texture = new ResourceLocation("blockbuster.actors", name + "/" + skins.keySet().iterator().next());
                }
            }
        }
    }

    public static class DummyEntity extends EntityLivingBase
    {
        public DummyEntity(World worldIn)
        {
            super(worldIn);
        }

        @Override
        public Iterable<ItemStack> getArmorInventoryList()
        {
            return null;
        }

        @Override
        public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
        {
            return null;
        }

        @Override
        public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
        {}

        @Override
        public EnumHandSide getPrimaryHand()
        {
            return null;
        }
    }
}