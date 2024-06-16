package net.anawesomguy.carnivalfoods.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CottonCandyItem extends Item {
    public CottonCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (world instanceof ServerWorld serverWorld) {
            stack.damage(1, serverWorld, user instanceof ServerPlayerEntity player ? player : null, item -> {});
            if (stack.isEmpty())
                return stack.copyComponentsToNewStackIgnoreEmpty(Items.SUGAR, 1);
        }
        return stack;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }
}
