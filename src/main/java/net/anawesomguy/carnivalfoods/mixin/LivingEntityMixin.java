package net.anawesomguy.carnivalfoods.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.anawesomguy.carnivalfoods.item.CottonCandyItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapWithCondition(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrementUnlessCreative(ILnet/minecraft/entity/LivingEntity;)V"))
    private boolean wrapEatDecrement(ItemStack stack, int amount, LivingEntity entity) {
        return !(stack.getItem() instanceof CottonCandyItem);
    }
}
