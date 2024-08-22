package net.anawesomguy.carnivalfoods.item;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.World;

/**
 * Base for items that can be used on a {@link CottonCandyMachineBlock}.
 */
public abstract class CottonCandyMachineUsable extends Item {
    public CottonCandyMachineUsable(Settings settings) {
        super(settings);
    }

    protected UseAction superGetUseAction(ItemStack stack) {
        return super.getUseAction(stack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks < getMaxUseTime(stack, user) &&
            remainingUseTicks % CottonCandyMachineBlock.TIME_FOR_ONE_LAYER == 0) {
            ItemStack sugars;
            if (user instanceof PlayerEntity player &&
                raycast(player) instanceof BlockHitResult blockHitResult &&
                blockHitResult.getType() == Type.BLOCK &&
                world.getBlockEntity(blockHitResult.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine &&
                !(sugars = machine.getStack(16)).isEmpty()) {
                int color = machine.getCraftedColor(stack);
                if (stack.isOf(CarnivalFoods.COTTON_CANDY)) {
                    stack.setDamage(stack.getDamage() - 1);
                    if (color != -1)
                        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, false));
                } else {
                    ItemStack newStack = stack.copyComponentsToNewStack(CarnivalFoods.COTTON_CANDY, 1);
                    newStack.setDamage(newStack.getMaxDamage() - 1);
                    stack.decrement(1);
                    if (stack.getCount() < 1)
                        player.setStackInHand(player.getActiveHand(), newStack);
                    else if (player instanceof ServerPlayerEntity)
                        player.getInventory().offerOrDrop(newStack);
                }
                sugars.decrement(1);
            } else
                user.stopUsingItem();
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        usageTick(world, user, (stack = super.finishUsing(stack, world, user)), 0);
        return stack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        if (player != null &&
            world.getBlockEntity(context.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine)
            if (machine.getStack(16).isEmpty())
                player.sendMessage(Text.translatable("message.carnival-foods.cotton_candy_machine_fail"), true);
            else {
                player.setCurrentHand(context.getHand());
                return ActionResult.CONSUME;
            }

        return ActionResult.PASS;
    }

    protected HitResult raycast(PlayerEntity player) {
        return ProjectileUtil.getCollision(player, entity -> !entity.isSpectator() && entity.canHit(),
                                           player.getBlockInteractionRange());
    }
}
