package net.anawesomguy.carnivalfoods.block.entity;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.ColorHelper.Argb;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CottonCandyMachineBlockEntity extends BlockEntity implements BasicInventory {
    public static final BlockEntityType<CottonCandyMachineBlockEntity> TYPE =
        new BlockEntityType<>(CottonCandyMachineBlockEntity::new, Set.of(CarnivalFoods.COTTON_CANDY_MACHINE), null);

    protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(17, ItemStack.EMPTY);// 0-15 is dyes, 16 is sugar

    public CottonCandyMachineBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    @NotNull
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getMaxCount(ItemStack stack) {
        return stack.getItem() == Items.SUGAR ? 3 : BasicInventory.super.getMaxCount(stack);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof DyeItem || (item == Items.SUGAR && slot == 16);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, items, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, items, registryLookup);
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(items);
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(items));
    }

    public int getCraftedColor() {
        int reds = 0, greens = 0, blues = 0, i = 0, count = 0;

        for (int j = 0; j < 16; j++)
            if (getStack(j).getItem() instanceof DyeItem dyeItem) {
                int color = dyeItem.getColor().getEntityColor();
                int red = Argb.getRed(color);
                int green = Argb.getGreen(color);
                int blue = Argb.getBlue(color);
                i += NumberUtils.max(red, green, blue);
                reds += red;
                greens += green;
                blues += blue;
                count++;
            }

        if (count == 0)
            return -1;

        int red = reds / count;
        int green = greens / count;
        int blue = blues / count;
        float f = (float)i / (float)count;
        float g = NumberUtils.max(red, green, blue);
        red = (int)((float)red * f / g);
        green = (int)((float)green * f / g);
        blue = (int)((float)blue * f / g);
        return ColorHelper.Argb.getArgb(red, green, blue);
    }
}
