package net.anawesomguy.carnivalfoods.mixin;

import net.anawesomguy.carnivalfoods.event.RenderCrosshairCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0, shift = Shift.AFTER))
    private void ivnokeCrosshairRenderEvent(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        RenderCrosshairCallback.EVENT.invoker().renderCrosshair(context, tickCounter);
    }
}
