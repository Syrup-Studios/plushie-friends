package net.syrupstudios.plushiefriends.loaders.fabric;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.syrupstudios.plushiefriends.PlushieFriends;
import net.syrupstudios.plushiefriends.data.PlushieDataManager;
import net.syrupstudios.plushiefriends.client.PlushieFriendsClient;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;

/**
 * Fabric's entry point for both the common and client initialization phases.
 */
public final class FabricEntrypoint implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        PlushieFriends.createContent();
        Registry.register(BuiltInRegistries.BLOCK, PlushieFriends.id("plushie"), PlushieFriends.PLUSHIE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, PlushieFriends.id("plushie"), PlushieFriends.PLUSHIE_ITEM);
        Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                PlushieFriends.id("plushie_be"),
                PlushieFriends.PLUSHIE_BLOCK_ENTITY
        );
        Registry.register(
                BuiltInRegistries.LOOT_FUNCTION_TYPE,
                PlushieFriends.id("set_plushie"),
                PlushieFriends.SET_PLUSHIE_FUNCTION
        );

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PlushieDataManager());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> PlushieDataManager.preloadProfiles());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {
            if (success) {
                PlushieDataManager.preloadProfiles();
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PlushieProfileManager.clearCache());
        PlushieFriends.initialized("Fabric");
    }

    @Override
    public void onInitializeClient() {
        new PlushieFriendsClient().onInitializeClient();
    }
}
//?}
