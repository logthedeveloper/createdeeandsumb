package com.logthedeveloper.tweedledeentweedledum.client;

import com.logthedeveloper.tweedledeentweedledum.ModAttachments;
import com.logthedeveloper.tweedledeentweedledum.TweedleMod;
import com.logthedeveloper.tweedledeentweedledum.network.TakeOffPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@EventBusSubscriber(modid = TweedleMod.MODID, value = Dist.CLIENT)
public class ClientGameEvents {

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        Player player = event.getEntity();
        if (event.getHand() != InteractionHand.MAIN_HAND || !player.isShiftKeyDown()) {
            return;
        }
        if (!player.getOffhandItem().isEmpty() || player.getData(ModAttachments.SHOULDER).isEmpty()) {
            return;
        }
        PacketDistributor.sendToServer(new TakeOffPayload());
    }
}
