
package com.logthedeveloper.tweedledeentweedledum.client;

import com.logthedeveloper.tweedledeentweedledum.TweedleMod;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = TweedleMod.MODID,
        value = Dist.CLIENT
)
public class ClientModEvents {

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {

        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new ShoulderPlushLayer(renderer));
            }
        }
    }
}
