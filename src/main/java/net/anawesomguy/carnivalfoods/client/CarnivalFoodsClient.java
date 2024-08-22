package net.anawesomguy.carnivalfoods.client;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.anawesomguy.carnivalfoods.client.render.CottonCandyMachineRenderer;
import net.anawesomguy.carnivalfoods.event.RenderCrosshairCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.ColorHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public final class CarnivalFoodsClient implements ClientModInitializer {
    private static final Map<Item, Identifier> HELD_ITEMS_PRIVATE = new WeakHashMap<>();
    public static final Map<Item, Identifier> HELD_ITEM_MODELS = Collections.unmodifiableMap(HELD_ITEMS_PRIVATE);
    public static final Identifier COTTON_CANDY_HELD = CarnivalFoods.id("item/cotton_candy_held");

    public static void addHeldItemModel(Item item, Identifier modelIdentifier) {
        HELD_ITEMS_PRIVATE.put(Objects.requireNonNull(item), modelIdentifier);
    }

    private final CottonCandyMachineBlockEntity renderCottonCandyMachine = CottonCandyMachineBlockEntity.createDummy();

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(CottonCandyMachineRenderer.LAYER_LOCATION,
                                                    CottonCandyMachineRenderer::createBodyLayer);
        BlockEntityRendererFactories.register(CottonCandyMachineBlockEntity.TYPE, CottonCandyMachineRenderer::new);
        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(HELD_ITEMS_PRIVATE.values()));

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            DyedColorComponent color;
            if (tintIndex == 1 && (color = stack.get(DataComponentTypes.DYED_COLOR)) != null)
                return ColorHelper.Argb.fullAlpha(color.rgb());
            return -1;
        }, CarnivalFoods.COTTON_CANDY);

        BuiltinItemRendererRegistry.INSTANCE.register(
            CarnivalFoods.COTTON_CANDY_MACHINE_ITEM,
            (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                if (MinecraftClient.getInstance()
                                   .getBlockEntityRenderDispatcher()
                                   .get(renderCottonCandyMachine) instanceof CottonCandyMachineRenderer renderer) {
                    renderer.renderStationary(matrices, vertexConsumers, light, overlay);
                }
            }
        );

        RenderCrosshairCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world;
            if (client.crosshairTarget instanceof BlockHitResult blockHit &&
                blockHit.getType() == Type.BLOCK &&
                (world = client.world) != null &&
                world.getBlockEntity(blockHit.getBlockPos()) instanceof CottonCandyMachineBlockEntity machine) {
                int width = context.getScaledWindowWidth(), height = context.getScaledWindowHeight();
                TextRenderer textRenderer = client.textRenderer;
                // draw the stack
                ItemStack stack = machine.getStack(16);
                if (!stack.isEmpty()) {
                    context.getMatrices().push();
                    context.drawItem(stack, (width - 47) / 2, (height - 16) / 2);

                    if (stack.getCount() != 1) {
                        String string = Integer.toString(stack.getCount());
                        context.getMatrices().translate(0.0F, 0.0F, 200.0F);
                        context.drawText(textRenderer, string, (width - 47) / 2 + 17 - textRenderer.getWidth(string), (height - 16) / 2 + 9, 16777215, true);
                    }

                    if (stack.isItemBarVisible()) {
                        int barX = (width - 47) / 2 + 2;
                        int barY = (height - 16) / 2 + 13;
                        context.fill(RenderLayer.getGuiOverlay(), barX, barY, barX + 13, barY + 2, Colors.BLACK);
                        context.fill(RenderLayer.getGuiOverlay(), barX, barY, barX + stack.getItemBarStep(),
                                     barY + 1, stack.getItemBarColor() | Colors.BLACK);
                    }

                    context.getMatrices().pop();
                }
                // draw crafted color text
                int color = machine.getCraftedColor(null); // should be opaque
                if (color != -1) {
                    Text text = Text.translatable("message.carnival-foods.color");
                    int textWidth = textRenderer.getWidth(text) / 2;
                    String colorText = "#" + Integer.toHexString(color).substring(2).toUpperCase();
                    int colorWidth = textRenderer.getWidth(colorText) / 2;
                    int centerX = width / 2;
                    int y = (height + 25) / 2;
                    context.drawText(textRenderer, text, centerX - textWidth - colorWidth, y, 16777215, true);
                    context.drawText(textRenderer, colorText, centerX + textWidth - colorWidth, y, color, true);
                }
            }
        });
    }

    static {
        addHeldItemModel(CarnivalFoods.COTTON_CANDY, COTTON_CANDY_HELD);
    }
}
