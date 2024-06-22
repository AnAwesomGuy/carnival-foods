package net.anawesomguy.carnivalfoods.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin implements UnbakedModel {
    @Shadow @Final
    private List<ModelElement> elements;
    @Unique
    boolean inheritElements;

    @SuppressWarnings({"LocalMayBeArgsOnly", "DataFlowIssue"}) // what? (neither warning makes any sense)
    @Inject(
        method = "setParents",
        at = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 0),
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;parent:Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"))
    ) // targets right before the for loop loops over, after `parent` is set
    private void addInheritedElements(Function<Identifier, UnbakedModel> modelLoader, CallbackInfo ci, @Local JsonUnbakedModel jsonModel, @Local UnbakedModel unbakedModel) {
        JsonUnbakedModelMixin jsonModelMixin = (JsonUnbakedModelMixin)(Object)jsonModel;
        if (jsonModelMixin.inheritElements && !jsonModelMixin.elements.isEmpty())
            jsonModelMixin.elements.addAll(((JsonUnbakedModel)unbakedModel).getElements());
    }

    @Mixin(JsonUnbakedModel.Deserializer.class)
    public static abstract class DeserializerMixin implements JsonDeserializer<JsonUnbakedModel> {
        @SuppressWarnings("ReferenceToMixin") // it's in a mixin
        @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;", at = @At("RETURN"))
        private void setInheritElements(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<JsonUnbakedModel> cir) {
            ((JsonUnbakedModelMixin)(Object)cir.getReturnValue()).inheritElements =
                ((JsonObject)jsonElement).getAsJsonPrimitive("inherit_elements").getAsBoolean();
        }
    }
}
