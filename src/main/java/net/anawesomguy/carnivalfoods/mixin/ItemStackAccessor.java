package net.anawesomguy.carnivalfoods.mixin;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {
    @Invoker("copyComponentsToNewStackIgnoreEmpty")
    ItemStack copyComponentsIgnoreEmpty(ItemConvertible item, int count);
}
