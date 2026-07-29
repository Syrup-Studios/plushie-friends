package net.syrupstudios.plushiefriends.loaders.fabric;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.syrupstudios.plushiefriends.PlushieFriends;
import net.syrupstudios.plushiefriends.client.PlushieFriendsClient;

/**
 * Fabric's entry point for both the common and client initialization phases.
 */
public final class FabricEntrypoint implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        new PlushieFriends().onInitialize();
    }

    @Override
    public void onInitializeClient() {
        new PlushieFriendsClient().onInitializeClient();
    }
}
//?}
