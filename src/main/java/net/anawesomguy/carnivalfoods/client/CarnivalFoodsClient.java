package net.anawesomguy.carnivalfoods.client;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.anawesomguy.carnivalfoods.client.render.CottonCandyMachineRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class CarnivalFoodsClient implements ClientModInitializer {
    private static final Map<Item, Identifier> HELD_ITEMS_PRIVATE = new WeakHashMap<>();
    public static final Map<Item, Identifier> HELD_ITEM_MODELS = Collections.unmodifiableMap(HELD_ITEMS_PRIVATE);
    public static final Identifier COTTON_CANDY_HELD = CarnivalFoods.id("item/cotton_candy_held");

    public static void addHeldItemModel(Item item, Identifier modelIdentifier) {
        HELD_ITEMS_PRIVATE.put(Objects.requireNonNull(item), modelIdentifier);
    }

    private final CottonCandyMachineBlockEntity renderCottonCandyMachine =
        new CottonCandyMachineBlockEntity(BlockPos.ORIGIN, CarnivalFoods.COTTON_CANDY_MACHINE.getDefaultState());

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(CottonCandyMachineRenderer.LAYER_LOCATION, CottonCandyMachineRenderer::createBodyLayer);
        BlockEntityRendererFactories.register(CottonCandyMachineBlockEntity.TYPE, CottonCandyMachineRenderer::new);
        ModelPredicateProviderRegistry.register(CarnivalFoods.COTTON_CANDY, CarnivalFoods.id("size"), (stack, world, entity, seed) -> (float)(stack.getDamage() / 2));
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            DyedColorComponent color;
            if (tintIndex == 1 && (color = stack.get(DataComponentTypes.DYED_COLOR)) != null)
                return ColorHelper.Argb.withAlpha(127, color.rgb());
            return -1;
        }, CarnivalFoods.COTTON_CANDY);
        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(HELD_ITEMS_PRIVATE.values()));
        BuiltinItemRendererRegistry.INSTANCE.register(
            CarnivalFoods.COTTON_CANDY_MACHINE_ITEM,
            (stack, mode, matrices, vertexConsumers, light, overlay) ->
                MinecraftClient.getInstance()
                               .getBlockEntityRenderDispatcher()
                               .renderEntity(renderCottonCandyMachine, matrices, vertexConsumers, light, overlay)
        );
    }

    static {
        addHeldItemModel(CarnivalFoods.COTTON_CANDY, COTTON_CANDY_HELD);
    }
}
