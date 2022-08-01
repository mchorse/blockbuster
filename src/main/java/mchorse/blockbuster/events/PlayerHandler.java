package mchorse.blockbuster.events;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.SkinHandler;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.function.Function;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PlayerHandler
{
    private Function<GunProps, Boolean> leftHandler = (props) -> props.preventLeftClick;
    private Function<GunProps, Boolean> rightHandler = (props) -> props.preventRightClick;
    private Function<GunProps, Boolean> attackHandler = (props) -> props.preventEntityAttack;

    private int timer;
    private int skinsTimer;

    private static NonNullList<ItemStack> mainInventoryBefore = NonNullList.withSize(36, ItemStack.EMPTY);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event)
    {
        Entity source = event.getSource().getTrueSource();

        if (source instanceof EntityPlayer)
        {
            this.handle((EntityPlayer) source, event, attackHandler);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event)
    {
        this.handle(event.getEntityPlayer(), event, leftHandler);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event)
    {
        this.handle(event.getEntityPlayer(), event, rightHandler);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        this.handle(event.getEntityPlayer(), event, rightHandler);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickItem event)
    {
        this.handle(event.getEntityPlayer(), event, rightHandler);
    }

    private static void preventItemPickUpScenePlayback(InventoryPlayer inventoryPlayer)
    {
        for (Map.Entry<String, Scene> scene : CommonProxy.scenes.getScenes().entrySet())
        {
            for (EntityPlayer player : scene.getValue().getTargetPlaybackPlayers())
            {
                if (player.inventory == inventoryPlayer)
                {
                    preventItemPickUp(player);
                }
            }
        }
    }

    /**
     * Reset the inventory to what it was before the item was picked up.
     * This also absorbs the dropped item
     */
    public static void preventItemPickUp(EntityPlayer player)
    {
        for (int i = 0; i < player.inventory.mainInventory.size(); i++)
        {
            ItemStack itemStackNow = player.inventory.mainInventory.get(i);
            ItemStack itemStackBefore = mainInventoryBefore.get(i);

            if (!ItemStack.areItemStacksEqual(itemStackNow, itemStackBefore))
            {
                player.inventory.mainInventory.set(i, itemStackBefore);
            }
        }
    }

    /**
     * Called by ASM {@link mchorse.blockbuster.core.transformers.EntityItemTransformer}
     * before item pick up event is fired and before the item is added to the inventory
     * @param entity
     * @param itemStack
     */
    public static void beforePlayerItemPickUp(EntityPlayer entity, ItemStack itemStack)
    {
    }

    public static void beforeItemStackAdd(InventoryPlayer inventory)
    {
        for (int i = 0; i < inventory.mainInventory.size(); i++)
        {
            ItemStack copy = inventory.mainInventory.get(i).copy();

            mainInventoryBefore.set(i, copy);
        }
    }

    public static void afterItemStackAdd(InventoryPlayer inventory)
    {
        preventItemPickUpScenePlayback(inventory);
    }

    private void handle(EntityPlayer player, LivingEvent event, Function<GunProps, Boolean> handler)
    {
        ItemStack stack = player.getHeldItemMainhand();

        if (!(stack.getItem() instanceof ItemGun))
        {
            return;
        }

        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        if (handler.apply(props) && event.isCancelable())
        {
            event.setCanceled(true);

            if (event instanceof PlayerInteractEvent)
            {
                ((PlayerInteractEvent) event).setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            /* Update TEs in the model's TEISR */
            if (event.player.world.isRemote)
            {
                this.updateClient();
            }
            else
            {
                if (this.timer % 100 == 0)
                {
                    StructureMorph.checkStructures();

                    this.timer = 0;
                }

                this.timer += 1;
            }

            return;
        }

        EntityPlayer player = event.player;
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.getItem() instanceof ItemGun)
        {
            ItemGun.decreaseReload(stack, player);
            ItemGun.decreaseTime(stack, player);
            ItemGun.checkGunState(stack, player);
            ItemGun.checkGunReload(stack, player);
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateClient()
    {
        for (TileEntityModelItemStackRenderer.TEModel model : TileEntityModelItemStackRenderer.models.values())
        {
            model.model.update();
        }

        for (TileEntityGunItemStackRenderer.GunEntry model : TileEntityGunItemStackRenderer.models.values())
        {
            model.props.update();
        }

        if (this.skinsTimer++ >= 30)
        {
            SkinHandler.checkSkinsFolder();
            this.skinsTimer = 0;
        }

        RenderingHandler.updateEmitters();
        GifTexture.updateTick();
    }
}