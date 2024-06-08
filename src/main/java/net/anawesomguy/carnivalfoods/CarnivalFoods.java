package net.anawesomguy.carnivalfoods;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class CarnivalFoods implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("carnival-foods");
    private static final Map<Item, ModelIdentifier> HELD_ITEMS_PRIVATE = new WeakHashMap<>();
    public static final Map<Item, ModelIdentifier> HELD_ITEM_MODELS = Collections.unmodifiableMap(HELD_ITEMS_PRIVATE);

    public static final FoodComponent COTTON_CANDY_FOOD = new FoodComponent.Builder()
        .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 5), .5F)
        .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 2), .3F)
        .usingConvertsTo(Items.STICK)
        .nutrition(1)
        .saturationModifier(.2F)
        .alwaysEdible()
        .snack()
        .build();
    public static final Item COTTON_CANDY = new Item(new Item.Settings().food(COTTON_CANDY_FOOD)) {
        @Override
        public UseAction getUseAction(ItemStack stack) {
            return UseAction.CROSSBOW;
        }
    };

    public static void addHeldItemModel(Item item, ModelIdentifier modelIdentifier) {
        HELD_ITEMS_PRIVATE.put(Objects.requireNonNull(item), modelIdentifier);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, Identifier.of("carnival-foods", "cotton_candy"), COTTON_CANDY);
    }

    static {
        HELD_ITEMS_PRIVATE.put(COTTON_CANDY, ModelIdentifier.ofInventoryVariant(Identifier.of("carnival-foods", "items/held_cotton_candy")));
    }
}