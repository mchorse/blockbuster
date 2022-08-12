package mchorse.blockbuster.events;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemBlockModel;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.blockbuster_pack.morphs.LightMorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PlayerTickHandler
{
    private Function<GunProps, Boolean> leftHandler = (props) -> props.preventLeftClick;
    private Function<GunProps, Boolean> rightHandler = (props) -> props.preventRightClick;
    private Function<GunProps, Boolean> attackHandler = (props) -> props.preventEntityAttack;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event)
    {
        Entity source = event.getSource().getTrueSource();

        if (source instanceof EntityPlayer)
        {
            this.handle((EntityPlayer) source, event, attackHandler);
        }
    }

    @SubscribeEvent
    public void onItemStackPickUp(PlayerEvent.ItemPickupEvent event)
    {
        this.updateLightMorphItemStack(event.getStack(), event.player);
    }

    @SubscribeEvent
    public void onItemStackToss(ItemTossEvent event)
    {
        this.updateLightMorphItemStack(event.getEntityItem().getItem(), event.getPlayer());
    }

    /**
     * Minecraft has a mechanism which prevents new ItemStack instances
     * when the NBT is equal. For the light morph this results in issues of the entity spawning.
     * Add a UUID to an ItemStack containing a lightmorph to force a new ItemStack instance.
     */
    protected void updateLightMorphItemStack(ItemStack itemStack, EntityLivingBase target)
    {
        NBTTagCompound tag = itemStack.getTagCompound();

        if (itemStack.getItem() instanceof ItemBlockModel && tag != null && tag.hasKey("BlockEntityTag"))
        {
            NBTTagCompound tileEntityNBT = tag.getCompoundTag("BlockEntityTag");

            if (!tileEntityNBT.hasKey("Morph"))
            {
                return;
            }

            AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(tileEntityNBT.getCompoundTag("Morph"));
            AbstractMorph morphTmp = MorphManager.INSTANCE.morphFromNBT(tileEntityNBT.getCompoundTag("Morph"));

            morphTmp.update(target);

            if (MorphUtils.anyMatch(morphTmp, (element) -> element instanceof LightMorph))
            {
                /* LightMorph.toNBT() adds a random UUID value - therefore update the NBT tag */
                tileEntityNBT.setTag("Morph", morph.toNBT());
            }
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
}