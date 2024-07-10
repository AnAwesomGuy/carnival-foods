package net.anawesomguy.carnivalfoods.block;

import com.mojang.serialization.MapCodec;
import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.anawesomguy.carnivalfoods.item.CottonCandyItem;
import net.anawesomguy.carnivalfoods.item.CottonCandyMachineUsable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CottonCandyMachineBlock extends BlockWithEntity {
    public static final int START_SPIN_TIME = 16;
    public static final int TIME_FOR_ONE_LAYER = 24;

    protected static final VoxelShape SHAPE = VoxelShapes.union(
        createCuboidShape(1, 1, 1, 15, 2, 15), // base
        createCuboidShape(0.8, 1, 1, 1.8, 3.5, 15), // outer lower
        createCuboidShape(14.2, 1, 1, 15.2, 3.5, 15),
        createCuboidShape(1, 1, 0.8, 15, 3.5, 1.8),
        createCuboidShape(1, 1, 14.2, 15, 3.5, 15.2),
        createCuboidShape(0.2, 3.5, 1, 1.2, 6, 15), // outer upper
        createCuboidShape(14.8, 3.5, 1, 15.8, 6, 15),
        createCuboidShape(1, 3.5, 0.2, 15, 6, 1.2),
        createCuboidShape(1, 3.5, 14.8, 15, 6, 15.8),
        createCuboidShape(2, 0, 2, 4, 1, 4), // feet
        createCuboidShape(2, 0, 12, 4, 1, 14),
        createCuboidShape(12, 0, 2, 14, 1, 4),
        createCuboidShape(12, 0, 12, 14, 1, 14)
    );

    public CottonCandyMachineBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CottonCandyMachineBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return world.isClient ? null :
            validateTicker(type, CottonCandyMachineBlockEntity.TYPE,
                           (world1, pos, state1, blockEntity) -> {
                               if (blockEntity.playerUsing == null)
                                   blockEntity.decrementSpeed();
                           });
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient || !player.getStackInHand(player.getActiveHand()).isEmpty())
            return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof CottonCandyMachineBlockEntity machine)
            for (int i = 0; i < 17; i++)
                if (!machine.getStack(i).isEmpty()) {
                    player.getInventory().offerOrDrop(machine.removeStack(i));
                    world.updateListeners(pos, state, state, NO_REDRAW);
                    break;
                }

        return ActionResult.SUCCESS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isEmpty())
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (world.getBlockEntity(pos) instanceof CottonCandyMachineBlockEntity machine) {
            Item item = stack.getItem();
            if (item instanceof CottonCandyMachineUsable) {
                if (machine.playerUsing == null || machine.playerUsing == player.getUuid()) {
                    if (item instanceof CottonCandyItem)
                        stack.set(CarnivalFoods.MARKER, Unit.INSTANCE);
                    machine.playerUsing = player.getUuid();
                }
            } else {
                int amount = 0;
                int max = machine.getMaxCount(stack);
                if (item == Items.SUGAR) {
                    ItemStack sugars = machine.getStack(16);
                    if (sugars.isEmpty()) {
                        amount = Math.min(machine.getMaxCount(stack), stack.getCount());
                        machine.setStack(16, stack.copyWithCount(amount));
                    } else
                        sugars.increment(amount = max - sugars.getCount());
                } else if (item instanceof DyeItem)
                    for (int i = 0; i < 16; i++) {
                        ItemStack dyes = machine.getStack(i);
                        if (dyes.isEmpty()) {
                            amount = Math.min(machine.getMaxCount(stack), stack.getCount());
                            machine.setStack(i, stack.copyWithCount(amount));
                            if (i == 15)
                                player.sendMessage(Text.translatable("message.carnival-foods.shame"), true);
                            break;
                        } else if (dyes.isOf(item)) {
                            dyes.increment(amount = max - dyes.getCount());
                            break;
                        }
                    }

                if (amount > 0) {
                    stack.decrement(amount);
                    world.updateListeners(pos, state, state, NO_REDRAW);
                }

                return ItemActionResult.SUCCESS;
            }
        }

        return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
