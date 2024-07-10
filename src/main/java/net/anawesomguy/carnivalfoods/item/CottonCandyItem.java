package net.anawesomguy.carnivalfoods.item;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
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
        return stack.contains(CarnivalFoods.MARKER) ? super.getUseAction(stack) : UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return stack.contains(CarnivalFoods.MARKER) ?
            CottonCandyMachineBlock.TIME_FOR_ONE_LAYER * stack.getDamage() :
            super.getMaxUseTime(stack, user);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return context.getStack().contains(CarnivalFoods.MARKER) ? super.useOnBlock(context) : ActionResult.PASS;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (stack.contains(CarnivalFoods.MARKER))
            super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.remove(CarnivalFoods.MARKER);
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack.remove(CarnivalFoods.MARKER);
        stack = super.finishUsing(stack, world, user); // there is a mixin to LivingEntity#eatFood so this works properly
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
}
