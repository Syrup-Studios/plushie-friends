package net.syrupstudios.plushiefriends.loot;

import com.mojang.authlib.GameProfile;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.nbt.CompoundTag;
import net.syrupstudios.plushiefriends.PlushieFriends;
import net.syrupstudios.plushiefriends.data.PlushieDataManager;
import net.syrupstudios.plushiefriends.util.PlushieNbtHelper;

public class SetPlushieFunction extends LootItemConditionalFunction {
    private final ResourceLocation plushieId;

    protected SetPlushieFunction(LootItemCondition[] predicates, ResourceLocation plushieId) {
        super(predicates);
        this.plushieId = plushieId;
    }

    @Override
    public LootItemFunctionType getType() {
        return PlushieFriends.SET_PLUSHIE_FUNCTION;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        PlushieDataManager.PlushieDefinition def = PlushieDataManager.get(this.plushieId);
        if (def != null) {
            CompoundTag itemTag = stack.getOrCreateTag();

            if (!def.ownerName().isEmpty()) {
                GameProfile profile = PlushieDataManager.getResolvedProfile(def.ownerName(), context.getLevel().getServer());
                if (profile != null) {
                    PlushieNbtHelper.writeOwnerToBlockEntityTag(itemTag, profile);
                } else {
                    PlushieNbtHelper.writeOwnerStringToBlockEntityTag(itemTag, def.ownerName());
                }
            }

            if (!def.lore().isEmpty()) {
                PlushieNbtHelper.writeLoreToBlockEntityTag(itemTag, def.lore());
            }
        }
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetPlushieFunction> {
        @Override
        public SetPlushieFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            ResourceLocation id = new ResourceLocation(json.get("id").getAsString());
            return new SetPlushieFunction(conditions, id);
        }

        @Override
        public void serialize(JsonObject json, SetPlushieFunction value, JsonSerializationContext context) {
            super.serialize(json, value, context);
            json.addProperty("id", value.plushieId.toString());
        }
    }
}
