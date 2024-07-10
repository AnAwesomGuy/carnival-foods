package net.anawesomguy.carnivalfoods.item;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import static net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock.START_SPIN_TIME;
import static net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock.TIME_FOR_ONE_LAYER;

public class CottonCandyItem extends CottonCandyMachineUsable {
    public CottonCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return stack.get(CarnivalFoods.MARKER) == null ? UseAction.NONE : super.getUseAction(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return stack.get(CarnivalFoods.MARKER) == null ?
            super.getMaxUseTime(stack, user) :
            START_SPIN_TIME + TIME_FOR_ONE_LAYER * stack.getDamage();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return context.getStack().get(CarnivalFoods.MARKER) == null ? ActionResult.PASS : super.useOnBlock(context);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (stack.get(CarnivalFoods.MARKER) != null)
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
        super.finishUsing(stack, world, user); // there is a mixin to LivingEntity#eatFood so this works properly
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
