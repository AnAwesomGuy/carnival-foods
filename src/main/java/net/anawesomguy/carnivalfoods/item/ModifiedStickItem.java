package net.anawesomguy.carnivalfoods.item;

import net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ModifiedStickItem extends CottonCandyMachineUsable {
    public ModifiedStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return CottonCandyMachineBlock.TIME_FOR_ONE_LAYER;
    }
}
