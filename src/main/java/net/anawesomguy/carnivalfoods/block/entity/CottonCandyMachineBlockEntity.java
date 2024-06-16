package net.anawesomguy.carnivalfoods.block.entity;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper.Argb;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CottonCandyMachineBlockEntity extends BlockEntity implements BasicInventory {
    public static final BlockEntityType<CottonCandyMachineBlockEntity> TYPE =
        new BlockEntityType<>(CottonCandyMachineBlockEntity::new, Set.of(), null);

    protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(17, ItemStack.EMPTY); // 0-15 is dyes, 16 is sugar

    public final InventoryStorage inventoryWrapper = InventoryStorage.of(this, null);

    public CottonCandyMachineBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override @NotNull
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

    @Nullable
    public ItemStack craft(ItemStack stack) {
        int reds = 0, greens = 0, blues = 0, i = 0, count = 0;
        int sugars = getStack(16).getCount();

        if (sugars == 0)
            return null;

        for (ItemStack itemStack : items)
            if (itemStack.getItem() instanceof DyeItem dyeItem) {
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

        int red = reds / count;
        int green = greens / count;
        int blue = blues / count;
        float f = (float)i / (float)count;
        float g = NumberUtils.max(red, green, blue);
        red = (int)((float)red * f / g);
        green = (int)((float)green * f / g);
        blue = (int)((float)blue * f / g);
        stack = stack.copyComponentsToNewStack(CarnivalFoods.COTTON_CANDY, 1);
        stack.setDamage(3 - MathHelper.clamp(sugars, 1, 3));
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(Argb.getArgb(red, green, blue), false));
        return stack;
    }

    private ItemStack setCottonCandy(ItemStack stack, int sugars) {
        stack = stack.copyComponentsToNewStack(CarnivalFoods.COTTON_CANDY, 1);
        stack.setDamage(3 - MathHelper.clamp(sugars, 1, 3));
        return stack;
    }
}
