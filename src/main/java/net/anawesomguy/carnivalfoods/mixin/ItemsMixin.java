package net.anawesomguy.carnivalfoods.mixin;

import net.anawesomguy.carnivalfoods.item.ModifiedStickItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;",
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=stick"
            )
        )
    )
    private static Item replaceStick(Item.Settings settings) {
        return new ModifiedStickItem(settings);
    }
}
