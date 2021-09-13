package mom.minecraft.momstruct.extension;

import mom.minecraft.momstruct.core.structure.VanillaStructure;
import mom.minecraft.momstruct.extension.Commands.StructureCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.extensions.Extension;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MomStructExtension extends Extension {
    public static ConcurrentHashMap<UUID, VanillaStructure> playerLoadedStructures;

    public static EventNode<PlayerEvent> events() {
        EventNode<PlayerEvent> node = EventNode.type("player-structure-events", EventFilter.PLAYER);

        node.addListener(PlayerDisconnectEvent.class, event -> {
            playerLoadedStructures.remove(event.getPlayer().getUuid());
        });

        return node;
    }

    @Override
    public void initialize() {
        System.out.println("MomStruct Extension initializing");

        playerLoadedStructures = new ConcurrentHashMap<>();

        getEventNode().addChild(events());

        MinecraftServer.getCommandManager().register(new StructureCommand());
    }

    @Override
    public void terminate() {
        playerLoadedStructures.clear();
    }
}
