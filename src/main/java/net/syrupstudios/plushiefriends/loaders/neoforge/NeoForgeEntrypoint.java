package net.syrupstudios.plushiefriends.loaders.neoforge;

//? if neoforge {
/*import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.syrupstudios.plushiefriends.PlushieFriends;
import net.syrupstudios.plushiefriends.data.PlushieDataManager;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;

/**
 * NeoForge's entry point.
 ^/
@Mod(PlushieFriends.MOD_ID)
public final class NeoForgeEntrypoint {
    public NeoForgeEntrypoint(IEventBus modBus) {
        modBus.addListener(this::registerContent);
        NeoForge.EVENT_BUS.addListener(this::addReloadListener);
        NeoForge.EVENT_BUS.addListener(this::serverStarted);
        NeoForge.EVENT_BUS.addListener(this::serverStopped);
        PlushieFriends.initialized("NeoForge");
    }

    private void registerContent(RegisterEvent event) {
        event.register(BuiltInRegistries.BLOCK.key(),
                helper -> helper.register(PlushieFriends.id("plushie"), PlushieFriends.createPlushieBlock()));
        event.register(BuiltInRegistries.ITEM.key(),
                helper -> helper.register(PlushieFriends.id("plushie"), PlushieFriends.createPlushieItem()));
        event.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(),
                helper -> helper.register(
                        PlushieFriends.id("plushie_be"),
                        PlushieFriends.createPlushieBlockEntity()
                ));
        event.register(BuiltInRegistries.LOOT_FUNCTION_TYPE.key(),
                helper -> helper.register(
                        PlushieFriends.id("set_plushie"),
                        PlushieFriends.createSetPlushieFunction()
                ));
    }

    private void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new PlushieDataManager());
    }

    private void serverStarted(ServerStartedEvent event) {
        PlushieDataManager.preloadProfiles();
    }

    private void serverStopped(ServerStoppedEvent event) {
        PlushieProfileManager.clearCache();
    }
}
*///?}
