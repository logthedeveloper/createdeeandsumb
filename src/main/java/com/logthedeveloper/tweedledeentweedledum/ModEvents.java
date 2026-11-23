package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@EventBusSubscriber(modid = TweedleMod.MODID)
public class ModEvents {
    private static final Set<String> WARNED_UNKNOWN_EFFECTS = new HashSet<>();


    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 20 != 0 || !Config.EFFECTS_ENABLED.get()) {
            return;
        }

        Set<PlushType> activePlushies = EnumSet.noneOf(PlushType.class);
        activePlushies.addAll(player.getData(ModAttachments.SHOULDER).worn());

        // Curios is fully optional. Its item-handler capability exists only when Curios is installed.
        for (ItemStack equipped : CuriosCompat.getTweedleStacks(player)) {
            if (equipped.getItem() instanceof PlushItem plushItem) {
                activePlushies.add(plushItem.getPlushType());
            }
        }

        if (activePlushies.isEmpty()) {
            return;
        }

        int duration = Config.EFFECT_DURATION_TICKS.get();
        boolean particles = Config.SHOW_PARTICLES.get();
        boolean icon = Config.SHOW_ICON.get();

        for (PlushType type : activePlushies) {
            for (String entry : Config.effectsFor(type)) {
                Config.EffectEntry parsed = Config.parseEffect(entry);
                if (parsed == null) {
                    continue;
                }
                Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(parsed.id());
                if (effect.isEmpty()) {
                    if (WARNED_UNKNOWN_EFFECTS.add(entry)) {
                        TweedleMod.LOGGER.warn("Unknown effect '{}' in the {} plush config, ignoring it.", entry, type.getSerializedName());
                    }
                    continue;
                }
                player.addEffect(new MobEffectInstance(effect.get(), duration, parsed.amplifier(), true, particles, icon));
            }
        }
    }


    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getState().getBlock() instanceof PlushBlock plushBlock)
                || !(event.getBlockEntity() instanceof PlushBlockEntity tankEntity)
                || tankEntity.getFluidTank().isEmpty()) {
            return;
        }

        ItemStack filledPlush = PlushItem.createWithBlockEntityData(plushBlock.getPlushType(), tankEntity.writeItemData(event.getLevel().registryAccess()));
        event.getDrops().clear();
        event.getDrops().add(new ItemEntity(event.getLevel(),
                event.getPos().getX() + 0.5D,
                event.getPos().getY() + 0.5D,
                event.getPos().getZ() + 0.5D,
                filledPlush));
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || keepsInventory(player)) {
            return;
        }
        ShoulderData data = player.getData(ModAttachments.SHOULDER);
        if (data.isEmpty()) {
            return;
        }
        if (data.left() != null) {
            ItemStack stack = PlushItem.createWithBlockEntityData(data.left(), data.leftItemData());
            event.getDrops().add(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack));
        }
        if (data.right() != null) {
            ItemStack stack = PlushItem.createWithBlockEntityData(data.right(), data.rightItemData());
            event.getDrops().add(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack));
        }
        player.setData(ModAttachments.SHOULDER, ShoulderData.EMPTY);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {

        if (!event.isWasDeath() || keepsInventory(event.getEntity())) {
            event.getEntity().setData(ModAttachments.SHOULDER, event.getOriginal().getData(ModAttachments.SHOULDER));
        }
    }

    private static boolean keepsInventory(Player player) {
        return player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
    }



    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ShoulderManager.sync(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ShoulderManager.sync(player);
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ShoulderManager.sync(player);
        }
    }


    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer viewer && event.getTarget() instanceof ServerPlayer target) {
            ShoulderManager.syncTo(viewer, target);
        }
    }
}
