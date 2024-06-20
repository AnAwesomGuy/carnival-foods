package net.anawesomguy.carnivalfoods;

import net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.anawesomguy.carnivalfoods.item.CottonCandyItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CarnivalFoods implements ModInitializer {
    public static final String MOD_ID = "carnival-foods";
    public static final Logger LOGGER = LoggerFactory.getLogger("carnival-foods");

    public static final FoodComponent COTTON_CANDY_FOOD = new FoodComponent.Builder()
        .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 3), .25F)
        .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 5), .55F)
        .usingConvertsTo(Items.STICK)
        .nutrition(1)
        .saturationModifier(.2F)
        .alwaysEdible()
        .snack()
        .build();
    public static final Block COTTON_CANDY_MACHINE = new CottonCandyMachineBlock(
        AbstractBlock.Settings.create()
                              .mapColor(MapColor.STONE_GRAY)
                              .requiresTool()
                              .strength(2F)
                              .nonOpaque()
    );
    public static final Item
        COTTON_CANDY = new CottonCandyItem(new Item.Settings().food(COTTON_CANDY_FOOD).maxDamage(3)),
        COTTON_CANDY_MACHINE_ITEM = new BlockItem(COTTON_CANDY_MACHINE, new Item.Settings());

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, id("cotton_candy"), COTTON_CANDY);

        Identifier cottonCandyMachine = id("cotton_candy_machine");
        Registry.register(Registries.BLOCK_ENTITY_TYPE, cottonCandyMachine, CottonCandyMachineBlockEntity.TYPE);
        Registry.register(Registries.BLOCK, cottonCandyMachine, COTTON_CANDY_MACHINE);
        Registry.register(Registries.ITEM, cottonCandyMachine, COTTON_CANDY_MACHINE_ITEM);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}