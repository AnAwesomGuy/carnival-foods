package net.anawesomguy.carnivalfoods.block;

import com.mojang.serialization.MapCodec;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CottonCandyMachineBlock extends BlockWithEntity {
    public static final int START_SPIN_TIME = 22;
    public static final int TIME_TO_MAKE_ONE_LAYER = 28;
    public static final int TOTAL_TIME = START_SPIN_TIME + 3 * TIME_TO_MAKE_ONE_LAYER;

    protected static final VoxelShape SHAPE = VoxelShapes.union(
        createCuboidShape(1,    1,   1,    15,   2,   15), // base
        createCuboidShape(2,    0,   2,    4,    1,   4), // feet
        createCuboidShape(2,    0,   12,   4,    1,   14),
        createCuboidShape(12,   0,   2,    14,   1,   4),
        createCuboidShape(12,   0,   12,   14,   1,   14),
        createCuboidShape(0.8,  1,   1,    1.8,  3.5, 15), // outer lower
        createCuboidShape(14.2, 1,   1,    15.2, 3.5, 15),
        createCuboidShape(1,    1,   0.8,  15,   3.5, 1.8),
        createCuboidShape(1,    1,   14.2, 15,   3.5, 15.2),
        createCuboidShape(0.2 , 3.5, 1,    1.2,  6,   15), // outer upper
        createCuboidShape(14.8, 3.5, 1,    15.8, 6,   15),
        createCuboidShape(1,    3.5, 0.2,  15,   6,   1.2),
        createCuboidShape(1,    3.5, 14.8, 15,   6,   15.8)
    );

    public CottonCandyMachineBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CottonCandyMachineBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient || !player.getMainHandStack().isEmpty())
            return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof CottonCandyMachineBlockEntity machine)
            for (int i = 0; i < 17; i++)
                if (!machine.getStack(i).isEmpty())
                    player.getInventory().offerOrDrop(machine.removeStack(i));

        return ActionResult.SUCCESS;
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
