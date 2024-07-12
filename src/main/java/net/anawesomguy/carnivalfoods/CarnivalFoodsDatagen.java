package net.anawesomguy.carnivalfoods;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

import static net.anawesomguy.carnivalfoods.CarnivalFoods.*;
import static net.minecraft.item.Items.*;

public final class CarnivalFoodsDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        Pack pack = dataGenerator.createPack();
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(LanguageProvider::new);
    }

    private static final class RecipeGenerator extends FabricRecipeProvider {
        private RecipeGenerator(FabricDataOutput output,
                                CompletableFuture<WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, COTTON_CANDY_MACHINE_ITEM)
                                   .input('#', Items.IRON_INGOT)
                                   .input('@', Items.IRON_BARS)
                                   .pattern("#@#")
                                   .pattern("###")
                                   .criterion("has_ingots", FabricRecipeProvider.conditionsFromItem(Items.IRON_INGOT))
                                   .criterion("has_bars", FabricRecipeProvider.conditionsFromItem(Items.IRON_BARS))
                                   .offerTo(exporter);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, HOTDOG_BUN)
                                      .input(BREAD)
                                      .criterion("has_bread", FabricRecipeProvider.conditionsFromItem(BREAD))
                                      .offerTo(exporter);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, HOTDOG)
                                      .input(ItemTags.SWORDS)
                                      .criterion("has_porkchop",
                                                 FabricRecipeProvider.conditionsFromItem(COOKED_PORKCHOP))
                                      .offerTo(exporter);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, HOTDOG_IN_BUN)
                                      .input(HOTDOG_BUN)
                                      .input(HOTDOG)
                                      .criterion("has_buns", FabricRecipeProvider.conditionsFromItem(HOTDOG_BUN))
                                      .criterion("has_hotdog", FabricRecipeProvider.conditionsFromItem(HOTDOG))
                                      .offerTo(exporter);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, VEGAN_HOTDOG)
                                      .input(HOTDOG_BUN)
                                      .input(KELP)
                                      .criterion("has_buns", FabricRecipeProvider.conditionsFromItem(HOTDOG_BUN))
                                      .criterion("has_kelp", FabricRecipeProvider.conditionsFromItem(KELP))
                                      .offerTo(exporter);
        }
    }

    private static final class LanguageProvider extends FabricLanguageProvider {
        private LanguageProvider(FabricDataOutput dataOutput, CompletableFuture<WrapperLookup> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(COTTON_CANDY_MACHINE, "Cotton Candy Machine");
            translationBuilder.add(COTTON_CANDY, "Cotton Candy");
            translationBuilder.add(HOTDOG_BUN, "Hotdog Bun");
            translationBuilder.add(HOTDOG, "Hotdog");
            translationBuilder.add(HOTDOG_IN_BUN, "Hotdog in Bun");
            translationBuilder.add(VEGAN_HOTDOG, "Vegan \"Hotdog\"");

            translationBuilder.add("message.carnival-foods.cotton_candy_machine_fail", "You need to add sugar!");
            translationBuilder.add("message.carnival-foods.shame", "Shame on you for mixing all the colors together, disgusting!");
            translationBuilder.add("message.carnival-foods.color", "Color: ");
        }
    }
}
