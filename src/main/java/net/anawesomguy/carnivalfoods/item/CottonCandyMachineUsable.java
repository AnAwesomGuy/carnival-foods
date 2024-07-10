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
import net.minecraft.server.world.ServerWorld;
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

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world instanceof ServerWorld) {
            ItemStack sugars;
            if (user instanceof PlayerEntity &&
                raycast((PlayerEntity)user) instanceof BlockHitResult blockHitResult &&
                blockHitResult.getType() == Type.BLOCK &&
                world.getBlockEntity(blockHitResult.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine &&
                !(sugars = machine.getStack(16)).isEmpty()) {
                if (!machine.incrementSpeed()) {
                    machine.playerUsing = null;
                    ItemStack newStack;
                    if (stack.isOf(CarnivalFoods.COTTON_CANDY))
                        (newStack = stack).setDamage(stack.getDamage() - 1);
                    else
                        newStack = CarnivalFoods.COTTON_CANDY.getDefaultStack();
                    int color = machine.getCraftedColor();
                    if (color > -1)
                        newStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, false));
                    sugars.decrement(1);
                    stack.decrement(1);
                    if (stack.getCount() < 1)
                        user.setStackInHand(user.getActiveHand(), newStack);
                }
            } else
                user.stopUsingItem();
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
        resetPlayerUsing(user, world);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        resetPlayerUsing(user, world);
        return super.finishUsing(stack, world, user);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null &&
            context.getWorld().getBlockEntity(context.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine &&
            (machine.playerUsing == null || machine.playerUsing == player.getUuid()))
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

    private void resetPlayerUsing(LivingEntity user, World world) {
        if (user instanceof ServerPlayerEntity &&
            raycast((PlayerEntity)user) instanceof BlockHitResult blockHitResult &&
            blockHitResult.getType() == Type.BLOCK &&
            world.getBlockEntity(blockHitResult.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine)
            machine.playerUsing = null;
    }
}
