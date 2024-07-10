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
        ItemStack sugars;
        if (user instanceof PlayerEntity player &&
            remainingUseTicks % CottonCandyMachineBlock.TIME_FOR_ONE_LAYER == 0 &&
            raycast(player) instanceof BlockHitResult blockHitResult &&
            blockHitResult.getType() == Type.BLOCK &&
            world.getBlockEntity(blockHitResult.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine &&
            !(sugars = machine.getStack(16)).isEmpty()) {
            ItemStack newStack;
            if (stack.isOf(CarnivalFoods.COTTON_CANDY))
                (newStack = stack).setDamage(stack.getDamage() - 1);
            else {
                newStack = CarnivalFoods.COTTON_CANDY.getDefaultStack();
                stack.decrement(1);
                if (stack.getCount() < 1)
                    player.setStackInHand(player.getActiveHand(), newStack);
                else
                    player.getInventory().offerOrDrop(stack);
            }
            int color = machine.getCraftedColor();
            if (color != -1)
                newStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, false));
            sugars.decrement(1);
        } else {
            stack.remove(CarnivalFoods.MARKER);
            user.stopUsingItem();
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack = super.finishUsing(stack, world, user);
        usageTick(world, user, stack, 0);
        return stack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null &&
            context.getWorld().getBlockEntity(context.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine)
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
