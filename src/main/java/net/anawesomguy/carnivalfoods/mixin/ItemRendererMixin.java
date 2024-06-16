package net.anawesomguy.carnivalfoods.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anawesomguy.carnivalfoods.client.CarnivalFoodsClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemModels;getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;"))
    private BakedModel renderHeldItem(ItemModels models, ItemStack stack, Operation<BakedModel> original) {
        ModelIdentifier model = CarnivalFoodsClient.HELD_ITEM_MODELS.get(stack.getItem());
        if (model != null)
            return models.getModelManager().getModel(model);
        return original.call(models, stack);
    }
}
