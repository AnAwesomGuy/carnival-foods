package net.anawesomguy.carnivalfoods.client.render;

import net.anawesomguy.carnivalfoods.CarnivalFoods;
import net.anawesomguy.carnivalfoods.block.entity.CottonCandyMachineBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;

import static net.minecraft.util.math.MathHelper.HALF_PI;
import static net.minecraft.util.math.MathHelper.PI;

@Environment(EnvType.CLIENT)
public class CottonCandyMachineRenderer implements BlockEntityRenderer<CottonCandyMachineBlockEntity> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(CarnivalFoods.id("cotton_candy_machine"), "main");
    @SuppressWarnings("deprecation") // BLOCK_ATLAS_TEXTURE
    public static final SpriteIdentifier TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, CarnivalFoods.id("entity/cotton_candy_machine"));

    private final ModelPart base;
    private final ModelPart spinning;

    public CottonCandyMachineRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart part = ctx.getLayerModelPart(LAYER_LOCATION);
        this.base = part.getChild("base");
        this.spinning = part.getChild("spinning");
    }

    public static TexturedModelData createBodyLayer() {
        ModelData modelData = new ModelData();
        ModelPartData data = modelData.getRoot();
        Dilation dilation = new Dilation(-0.25F);
        ModelPartBuilder outer = ModelPartBuilder.create().uv(0, 8).cuboid(-7, -6.2F, -6.89F, 14, 5F, 1F);
        ModelPartBuilder inner1 = ModelPartBuilder.create().uv(14, 0).cuboid(-1.5F, -3.8F, 3F, 3F, 5F, 1F, new Dilation(-0.2F));
        ModelPartBuilder inner2 = ModelPartBuilder.create().cuboid(-4, -4.1F, -1.5F, 1F, 5F, 3F, dilation);
        ModelPartBuilder inner3 = ModelPartBuilder.create().cuboid(3, -4.1F, -1.5F, 1F, 5F, 3F, dilation);

        ModelPartData base = data.addChild("base", ModelPartBuilder.create().uv(-14, 14).cuboid(-7F, -1F, -7F, 14F, 1F, 14F), ModelTransform.pivot(0F, 24F, 0F));
        base.addChild("outerN", outer, ModelTransform.rotation(PI / 18, 0F, 0F));
        base.addChild("outerS", ModelPartBuilder.create().uv(0, 8).cuboid(-7, -6.2F, 5.89F, 14, 5F, 1F), ModelTransform.rotation(-PI / 18, 0F, 0F));
        base.addChild("outerE", outer, ModelTransform.rotation(PI / 18, HALF_PI, 0F));
        base.addChild("outerW", outer, ModelTransform.rotation(PI / 18, -HALF_PI, 0F));
        base.addChild("innerN", inner1, ModelTransform.rotation(-PI / 1.125F, PI / 180F * 35F, PI));
        base.addChild("innerS", inner1, ModelTransform.rotation(PI / 9, -PI / 36F * 7F, 0F));
        base.addChild("innerE", inner1, ModelTransform.rotation(-PI / 1.125F, -PI / 36F * 11F, PI));
        base.addChild("innerW", inner1, ModelTransform.rotation(PI / 9, PI / 36F * 11F, 0F));
        base.addChild("innerNE", inner2, ModelTransform.rotation(-0.9727F, -1.2573F, 0.9958F));
        base.addChild("innerSE", inner2, ModelTransform.rotation(0.0456F, 0.1685F, 0.2657F));
        base.addChild("innerNW", inner3, ModelTransform.rotation(-0.0456F, 0.1685F, -0.2657F));
        base.addChild("innerSW", inner3, ModelTransform.rotation(0.9727F, -1.2573F, -0.9958F));

        data.addChild("spinning", ModelPartBuilder.create().uv(8, 0).cuboid(-1, -4.3F, -0.5F, 2F, 4F, 1), ModelTransform.rotation(0F, -PI / 8.6F, 0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(CottonCandyMachineBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world != null) {
            spinning.yaw = Math.clamp(spinning.yaw + tickDelta, 0, PI);
            VertexConsumer consumer = TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
            base.render(matrices, consumer, light, overlay);
        }
    }
}
