package net.anawesomguy.carnivalfoods.item;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;

import static net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock.*;

public class ModifiedStickItem extends Item {
    public ModifiedStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return TOTAL_TIME;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        CarnivalFoods.LOGGER.info(String.valueOf(remainingUseTicks));
        if (user instanceof PlayerEntity) {
            if (ProjectileUtil.getCollision(user, entity -> !entity.isSpectator() && entity.canHit(),
                                            ((PlayerEntity)user).getBlockInteractionRange()) instanceof BlockHitResult blockHitResult &&
                blockHitResult.getType() == Type.BLOCK &&
                world.getBlockEntity(blockHitResult.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine) {
                int time = remainingUseTicks - START_SPIN_TIME;
                if ((time >= 0) && (time % TIME_FOR_ONE_LAYER) == 0) {
                    int layers = time / TIME_FOR_ONE_LAYER;
                    Hand activeHand = user.getActiveHand();
                    if (layers == 0) {
                        ItemStack newStack = CarnivalFoods.COTTON_CANDY.getDefaultStack();
                        newStack.setDamage(newStack.getMaxDamage() - 1);
                        int color = machine.getCraftedColor();
                        if (color > -1)
                            newStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(machine.getCraftedColor(), false));
                        user.setStackInHand(activeHand, newStack);
                    } else {
                        ItemStack activeStack = user.getActiveItem();
                        activeStack.setDamage(activeStack.getDamage() - 1);
                    }
                }
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient &&
            world.getBlockEntity(context.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine) {
            if (machine.getStack(16).isEmpty())
                return ActionResult.PASS;
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        ActionResult result = useOnBlock(new ItemUsageContext(world, user, hand, stack,
                                                              raycast(world, user, FluidHandling.NONE)));
        return result.isAccepted() ? new TypedActionResult<>(result, stack) : super.use(world, user, hand);
    }
}
