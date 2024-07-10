package net.anawesomguy.carnivalfoods.block.entity;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper.Argb;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class CottonCandyMachineBlockEntity extends BlockEntity implements BasicInventory {
    public static final BlockEntityType<CottonCandyMachineBlockEntity> TYPE =
        new BlockEntityType<>(CottonCandyMachineBlockEntity::new, Set.of(CarnivalFoods.COTTON_CANDY_MACHINE), null);

    protected final DefaultedList<ItemStack> items; // 0-15 is dyes, 16 is sugar
    public UUID playerUsing;

    /**
     * A value between 0 and 32, indicating how fast the blade will spin and how far the progress of starting the spin is.
     *
     * @see CottonCandyMachineBlock#START_SPIN_TIME
     */
    protected byte speed;

    public CottonCandyMachineBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        items = DefaultedList.ofSize(17, ItemStack.EMPTY);
    }

    private CottonCandyMachineBlockEntity() {
        super(TYPE, BlockPos.ORIGIN, null);
        items = null;
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
    protected void readNbt(NbtCompound nbt, WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        items.clear();
        Inventories.readNbt(nbt, items, lookup);
        speed = nbt.getByte("speed");
        if (speed < (byte)0)
            speed = (byte)0;
        else if (speed > (byte)32)
            speed = (byte)32;
        NbtElement uuid = nbt.get("playerUsing");
        if (uuid != null)
            playerUsing = NbtHelper.toUuid(uuid);
        else
            playerUsing = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup lookup) {
        writeNbtWithoutPlayer(nbt, lookup);
        if (playerUsing != null)
            nbt.putUuid("playerUsing", playerUsing);
    }

    protected void writeNbtWithoutPlayer(NbtCompound nbt, WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, items, lookup);
        if (speed > (byte)0)
            nbt.putByte("speed", speed);
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
        NbtCompound nbt = new NbtCompound();
        writeNbtWithoutPlayer(nbt, lookup);
        return nbt;
    }

    public byte getSpeed() {
        return speed;
    }

    /**
     * @return {@code true} if speed was incremented.
     */
    public boolean incrementSpeed() {
        if (speed >= (byte)32)
            return false;
        speed += (byte)2;
        if (speed > (byte)32)
            speed = (byte)32;
        if (world instanceof ServerWorld serverWorld)
            serverWorld.getChunkManager().markForUpdate(pos);
        return true;
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
        return Argb.getArgb(red, green, blue);
    }

    public void decrementSpeed() {
        if (speed > 0) {
            speed--;
            if (world instanceof ServerWorld serverWorld)
                serverWorld.getChunkManager().markForUpdate(pos);
        }
    }

    @Internal
    public static CottonCandyMachineBlockEntity createDummy() {
        return new CottonCandyMachineBlockEntity();
    }
}
