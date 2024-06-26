package net.anawesomguy.carnivalfoods.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import static net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock.START_SPIN_TIME;
import static net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock.TIME_FOR_ONE_LAYER;

public class CottonCandyItem extends Item {
    public CottonCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (world instanceof ServerWorld serverWorld) {
            stack.damage(1, serverWorld, user instanceof ServerPlayerEntity player ? player : null, item -> {});
            if (stack.isEmpty())
                return Items.STICK.getDefaultStack();
        }
        return stack;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        int damage = stack.getDamage();
        return damage == 0 ?
            super.getMaxUseTime(stack, user) :
            START_SPIN_TIME + TIME_FOR_ONE_LAYER * stack.getDamage();
    }
}
