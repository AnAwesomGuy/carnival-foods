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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper.Argb;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CottonCandyMachineBlockEntity extends BlockEntity implements BasicInventory {
    public static final BlockEntityType<CottonCandyMachineBlockEntity> TYPE =
        new BlockEntityType<>(CottonCandyMachineBlockEntity::new, Set.of(CarnivalFoods.COTTON_CANDY_MACHINE), null);

    protected final DefaultedList<ItemStack> items; // 0-15 is dyes, 16 is sugar

    public CottonCandyMachineBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        items = DefaultedList.ofSize(17, ItemStack.EMPTY);
    }

    private CottonCandyMachineBlockEntity() {
        super(TYPE, BlockPos.ORIGIN, CarnivalFoods.COTTON_CANDY_MACHINE.getDefaultState());
        items = null;
    }

    @Override
    @NotNull
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getMaxCount(ItemStack stack) {
        return stack.getItem() == Items.SUGAR ? 16 : BasicInventory.super.getMaxCount(stack);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        Item item = stack.getItem();
        return slot == 16 ? item == Items.SUGAR : item instanceof DyeItem;
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        items.clear();
        Inventories.readNbt(nbt, items, lookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, items, lookup);
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(WrapperLookup lookup) {
        return createNbt(lookup);
    }

    /**
     * @param stack the stack used on the machine
     * @return the color that using this machine would result in
     */
    // adapted from DyedColorComponent
    public int getCraftedColor(@Nullable ItemStack stack) {
        int reds = 0, greens = 0, blues = 0, i = 0, count = 0;

        if (stack != null && !stack.isEmpty()) {
            DyedColorComponent dyedColorComponent = stack.get(DataComponentTypes.DYED_COLOR);
            if (dyedColorComponent != null) {
                int color = dyedColorComponent.rgb();
                int red = Argb.getRed(color);
                int green = Argb.getGreen(color);
                int blue = Argb.getBlue(color);
                i += NumberUtils.max(red, green, blue);
                reds += red;
                greens += green;
                blues += blue;
                count++;
            }
        }

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
        else if (count == 1)
            return Argb.getArgb(reds, greens, blues);

        int red = reds / count;
        int green = greens / count;
        int blue = blues / count;
        float f = (float)i / (float)count;
        float g = NumberUtils.max(red, green, blue);
        red = (int)((float)red * f / g);
        green = (int)((float)green * f / g);
        blue = (int)((float)blue * f / g);
        return Argb.getArgb(red, green, blue);
    }

    public static int mixColors(int a, int b) {
        return Argb.getArgb(
            ((a >> 16) + (b >> 16) & 0xFF) / 2,
            ((a >> 8) + (b >> 8) & 0xFF) / 2,
            (a + b & 0xFF) / 2
        );
    }

    /**
     * @return a dummy instance of this block entity. Calling methods on this instance will likely result in a crash.
     */
    @Internal
    public static CottonCandyMachineBlockEntity createDummy() {
        return new CottonCandyMachineBlockEntity();
    }
}
