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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

import static net.minecraft.client.model.ModelPartBuilder.create;
import static net.minecraft.util.math.MathHelper.HALF_PI;
import static net.minecraft.util.math.MathHelper.PI;

@Environment(EnvType.CLIENT)
public class CottonCandyMachineRenderer implements BlockEntityRenderer<CottonCandyMachineBlockEntity> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(CarnivalFoods.id("cotton_candy_machine"), "main");
    @SuppressWarnings("deprecation") // BLOCK_ATLAS_TEXTURE
    public static final SpriteIdentifier TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, CarnivalFoods.id("entity/cotton_candy_machine"));

    private final ModelPart bone;
    private final ModelPart spinning;

    public CottonCandyMachineRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart part = ctx.getLayerModelPart(LAYER_LOCATION);
        this.bone = part.getChild("bone");
        this.spinning = part.getChild("spinning");
    }

    public static TexturedModelData createBodyLayer() {
        ModelData modelData = new ModelData();
        ModelPartData data = modelData.getRoot();
        Dilation dilation1 = new Dilation(-0.01F);
        Dilation dilation2 = new Dilation(-0.25F);
        ModelPartBuilder
            outer = create().uv(0, 8).cuboid(-7F, 2F, -6.8F, 14F, 5F, 1F), // north, east, west
            inner1 = create().uv(14, 0).cuboid(-1.5F, -0.2F, 3.3F, 3F, 5F, 1F, new Dilation(-0.2F)), // nesw
            inner2 = create().cuboid(-4.2F, 0.1F, -1.35F, 1F, 5F, 3F, dilation2).mirrored(), // northeast, southeast
            inner3 = create().cuboid(3.2F, 0.1F, -1.65F, 1F, 5F, 3F, dilation2); // northwest, southwest

        ModelPartData bone = data.addChild(EntityModelPartNames.BONE, create(), ModelTransform.NONE);
        bone.addChild("bone",
            create().uv(-14, 14).cuboid(-7F, 1F, -7F, 14F, 1F, 14F, new Dilation(0.01F))
                    .uv(19, 4).cuboid(-6F, 0F, -6F, 2F, 2F, 2F, dilation1)
                    .uv(19, 4).cuboid(-6F, 0F, 4F, 2F, 2F, 2F, dilation1)
                    .uv(19, 4).cuboid(4F, 0F, 4F, 2F, 2F, 2F, dilation1)
                    .uv(19, 4).cuboid(4F, 0F, -6F, 2F, 2F, 2F, dilation1),
            ModelTransform.pivot(8, 0, 8));
        bone.addChild("outerN", outer, ModelTransform.rotation(PI / 180 * 7.8F, 0F, 0F));
        bone.addChild("outerS", create().uv(0, 8).cuboid(-7F, 2F, 5.8F, 14F, 5F, 1F), ModelTransform.rotation(-PI / 180 * 7.8F, 0F, 0F));
        bone.addChild("outerE", outer.mirrored(), ModelTransform.rotation(PI / 180 * 7.8F, HALF_PI, 0F));
        bone.addChild("outerW", outer, ModelTransform.rotation(PI / 180 * 7.8F, -HALF_PI, 0F));
        bone.addChild("innerE", inner1, ModelTransform.rotation(-PI / 1.125F, -PI / 36F * 11F, PI));
        bone.addChild("innerW", inner1, ModelTransform.rotation(PI / 9, PI / 36F * 11F, 0F));
        bone.addChild("innerN", inner1.mirrored(), ModelTransform.rotation(-PI / 1.125F, PI / 36F * 7F, PI));
        bone.addChild("innerS", inner1, ModelTransform.rotation(PI / 9, -PI / 36F * 7F, 0F));
        bone.addChild("innerNE", inner2, ModelTransform.rotation(-1.0601F, -1.2728F, 1.0791F));
        bone.addChild("innerSE", inner2, ModelTransform.rotation(0.0375F, 0.1391F, 0.2644F));
        bone.addChild("innerNW", inner3, ModelTransform.rotation(-0.0364F, 0.1348F, -0.2643F));
        bone.addChild("innerSW", inner3, ModelTransform.rotation(1.0601F, -1.2728F, -1.0791F));

        data.addChild("spinning", create().uv(8, 0).cuboid(7F, 1.3F, 7.5F, 2F, 4F, 1F), ModelTransform.rotation(0F, -PI / 7.9F, 0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(CottonCandyMachineBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        spinning.yaw = (spinning.yaw + tickDelta) % PI;
        VertexConsumer consumer = TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
        bone.render(matrices, consumer, light, overlay);
        spinning.render(matrices, consumer, light, overlay);
    }
}
