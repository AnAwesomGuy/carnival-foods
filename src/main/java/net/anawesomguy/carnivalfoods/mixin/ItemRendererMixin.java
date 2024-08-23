package net.anawesomguy.carnivalfoods.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.anawesomguy.carnivalfoods.client.CarnivalFoodsClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final
    private ItemModels models;

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModel;getTransformation()Lnet/minecraft/client/render/model/json/ModelTransformation;"))
    private void getHeldItemModel(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
                                  MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay,
                                  BakedModel model, CallbackInfo ci, @Local(ordinal = 1) boolean bl, @Local(argsOnly = true) LocalRef<BakedModel> modelRef) {
        if (!bl) {
            Identifier id = CarnivalFoodsClient.HELD_ITEM_MODELS.get(stack.getItem());
            if (id != null) {
                BakedModelManager manager = models.getModelManager();
                BakedModel heldModel = manager.getModel(id);
                modelRef.set(
                    heldModel == null ?
                        manager.getMissingModel() :
                        heldModel.getOverrides().apply(heldModel, stack, null, null, 0)
                );
            }
        }
    }
}
