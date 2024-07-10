package net.anawesomguy.carnivalfoods.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

@FunctionalInterface
public interface RenderCrosshairCallback {
    Event<RenderCrosshairCallback> EVENT = EventFactory.createArrayBacked(RenderCrosshairCallback.class, (listeners) -> (matrixStack, delta) -> {
        for (RenderCrosshairCallback event : listeners)
            event.renderCrosshair(matrixStack, delta);
    });

    void renderCrosshair(DrawContext context, RenderTickCounter tickCounter);
}
