package net.syrupstudios.plushiefriends.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin {

    private static ResourceLocation remapId(ResourceLocation id) {
        if (id != null && "plushie-friends".equals(id.getNamespace())) {
            return net.syrupstudios.plushiefriends.PlushieFriends.id(id.getPath());
        }
        return id;
    }

    private static ResourceKey<?> remapKey(ResourceKey<?> key) {
        if (key != null && "plushie-friends".equals(key.location().getNamespace())) {
            return ResourceKey.create(
                    ResourceKey.createRegistryKey(key.registry()),
                    net.syrupstudios.plushiefriends.PlushieFriends.id(key.location().getPath())
            );
        }
        return key;
    }

    // Redirects standard object lookups by ResourceLocation (e.g., loading items/blocks from chunk data or NBT tags)
    @ModifyVariable(method = "get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;", at = @At("HEAD"), argsOnly = true)
    private ResourceLocation remapGetId(ResourceLocation id) {
        return remapId(id);
    }

    // Redirects standard object lookups by ResourceKey
    @ModifyVariable(method = "get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;", at = @At("HEAD"), argsOnly = true)
    private ResourceKey<?> remapGetKey(ResourceKey<?> key) {
        return remapKey(key);
    }

    // Redirects registry holder lookups by ResourceKey
    @ModifyVariable(method = "getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;", at = @At("HEAD"), argsOnly = true)
    private ResourceKey<?> remapGetHolderKey(ResourceKey<?> key) {
        return remapKey(key);
    }

    // Redirects presence checks by ResourceLocation
    @ModifyVariable(method = "containsKey(Lnet/minecraft/resources/ResourceLocation;)Z", at = @At("HEAD"), argsOnly = true)
    private ResourceLocation remapContainsKeyId(ResourceLocation id) {
        return remapId(id);
    }

    // Redirects presence checks by ResourceKey
    @ModifyVariable(method = "containsKey(Lnet/minecraft/resources/ResourceKey;)Z", at = @At("HEAD"), argsOnly = true)
    private ResourceKey<?> remapContainsKeyKey(ResourceKey<?> key) {
        return remapKey(key);
    }
}
