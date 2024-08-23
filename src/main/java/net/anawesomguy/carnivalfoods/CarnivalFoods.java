package net.anawesomguy.carnivalfoods;

import net.anawesomguy.carnivalfoods.block.CottonCandyMachineBlock;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.anawesomguy.carnivalfoods.item.CottonCandyItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponent.Builder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class CarnivalFoods implements ModInitializer {
    public static final String MOD_ID = "carnival-foods";

    public static final FoodComponent
        COTTON_CANDY_FOOD = new Builder().statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 65), 0.5F)
                                         .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 1), 0.75F)
                                         .nutrition(1)
                                         .saturationModifier(0.2F)
                                         .alwaysEdible()
                                         .snack()
                                         .build(),
        HOTDOG_BUN_FOOD = new Builder().nutrition(3).saturationModifier(0.23F).build(),
        HOTDOG_FOOD = new Builder().nutrition(2).saturationModifier(0.3F).build(),
        HOTDOG_IN_BUN_FOOD = new Builder().nutrition(5).saturationModifier(0.57F).build(),
        VEGAN_HOTDOG_FOOD = new Builder().statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 40), 0.35F)
                                         .nutrition(4)
                                         .saturationModifier(0.3F)
                                         .build();

    public static final Block COTTON_CANDY_MACHINE = new CottonCandyMachineBlock(
        AbstractBlock.Settings.create()
                              .mapColor(MapColor.STONE_GRAY)
                              .requiresTool()
                              .strength(2F)
                              .nonOpaque()
    );

    public static final Item
        COTTON_CANDY_MACHINE_ITEM = new BlockItem(COTTON_CANDY_MACHINE, new Item.Settings()),
        COTTON_CANDY = new CottonCandyItem(new Item.Settings().food(COTTON_CANDY_FOOD).maxDamage(3)),
        HOTDOG_BUN = new Item(new Item.Settings().food(HOTDOG_BUN_FOOD)),
        HOTDOG = new Item(new Item.Settings().food(HOTDOG_FOOD)),
        HOTDOG_IN_BUN = new Item(new Item.Settings().food(HOTDOG_IN_BUN_FOOD)),
        VEGAN_HOTDOG = new Item(new Item.Settings().food(VEGAN_HOTDOG_FOOD));

    @Override
    public void onInitialize() {
        Identifier cottonCandyMachine = id("cotton_candy_machine");
        Registry.register(Registries.BLOCK_ENTITY_TYPE, cottonCandyMachine, CottonCandyMachineBlockEntity.TYPE);
        Registry.register(Registries.BLOCK, cottonCandyMachine, COTTON_CANDY_MACHINE);
        Registry.register(Registries.ITEM, cottonCandyMachine, COTTON_CANDY_MACHINE_ITEM);
        Registry.register(Registries.ITEM, id("cotton_candy"), COTTON_CANDY);
        Registry.register(Registries.ITEM, id("hotdog_bun"), HOTDOG_BUN);
        Registry.register(Registries.ITEM, id("hotdog"), HOTDOG);
        Registry.register(Registries.ITEM, id("hotdog_in_bun"), HOTDOG_IN_BUN);
        Registry.register(Registries.ITEM, id("vegan_hotdog"), VEGAN_HOTDOG);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                       .register(entries -> entries.add(COTTON_CANDY_MACHINE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(COTTON_CANDY);
            entries.add(HOTDOG_BUN);
            entries.add(HOTDOG);
            entries.add(HOTDOG_IN_BUN);
            entries.add(VEGAN_HOTDOG);
        });
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}