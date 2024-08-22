package net.anawesomguy.carnivalfoods.item;

import net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CottonCandyItem extends CottonCandyMachineUsable {
    public CottonCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return stack.contains(DataComponentTypes.FOOD) ? super.superGetUseAction(stack) : super.getUseAction(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        int time = super.getMaxUseTime(stack, user);
        return time > 0 ? time : CottonCandyMachineBlock.TIME_FOR_ONE_LAYER * stack.getDamage();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return context.getStack().contains(DataComponentTypes.FOOD) ? ActionResult.PASS : super.useOnBlock(context);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!stack.contains(DataComponentTypes.FOOD))
            super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
        stack.set(DataComponentTypes.FOOD, stack.getDefaultComponents().get(DataComponentTypes.FOOD));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack = super.finishUsing(stack, world, user); // there is a mixin to LivingEntity#eatFood so this works properly
        if (stack.contains(DataComponentTypes.FOOD)) {
            if (world instanceof ServerWorld) {
                stack = stack.damage(1, Items.STICK, user, LivingEntity.getSlotForHand(user.getActiveHand()));
                stack.remove(DataComponentTypes.DYED_COLOR);
            }
        } else
            stack.set(DataComponentTypes.FOOD, stack.getDefaultComponents().get(DataComponentTypes.FOOD));
        return stack;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }
}
