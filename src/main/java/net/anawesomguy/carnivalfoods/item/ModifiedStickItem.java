package net.anawesomguy.carnivalfoods.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import static net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock.START_SPIN_TIME;
import static net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock.TIME_FOR_ONE_LAYER;

public class ModifiedStickItem extends CottonCandyMachineUsable {
    public ModifiedStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return START_SPIN_TIME + TIME_FOR_ONE_LAYER;
    }
}
