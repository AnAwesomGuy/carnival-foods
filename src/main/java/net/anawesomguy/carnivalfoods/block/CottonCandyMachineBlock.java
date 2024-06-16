package net.anawesomguy.carnivalfoods.block;

import com.mojang.serialization.MapCodec;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CottonCandyMachineBlock extends BlockWithEntity {
    protected static final VoxelShape SHAPE = VoxelShapes.union(
        createCuboidShape(1, 0, 1, 15, 1, 15), // base
        createCuboidShape(0.5, 0, 1, 1.5, 2.5, 15), // outer lower
        createCuboidShape(13.5, 0, 1, 15.5, 2.5, 15),
        createCuboidShape(1, 0, 0.5, 15, 2.5, 1.5),
        createCuboidShape(1, 0, 13.5, 15, 2.5, 15.5),
        createCuboidShape(0.1, 2.5, 1, 1.1, 5, 15), // outer upper
        createCuboidShape(14.9, 2.5, 1, 15.9, 5, 15),
        createCuboidShape(0.1, 2.5, 1, 1.1, 5, 15),
        createCuboidShape(1, 2.5, 14.9, 15, 5, 15.9)
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
        if (world.isClient)
            return ActionResult.PASS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CottonCandyMachineBlockEntity machine) {
            for (int i = 0; i < 17; i++) {

            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
