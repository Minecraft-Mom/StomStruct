package mom.minecraft.stomstruct.extension;

import mom.minecraft.stomstruct.core.io.IStructureReader;
import mom.minecraft.stomstruct.core.io.IStructureWriter;
import mom.minecraft.stomstruct.core.io.VanillaStructureReader;
import mom.minecraft.stomstruct.core.io.VanillaStructureWriter;
import mom.minecraft.stomstruct.core.structure.Structure;
import mom.minecraft.stomstruct.extension.commands.StructureCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.extensions.Extension;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StomStructExtension extends Extension {
    public static final String STRUCTURE_DIR = "./structures/";

    public static IStructureReader structureReader = new VanillaStructureReader();
    public static IStructureWriter structureWriter = new VanillaStructureWriter();

    public static Command structureCommand = new StructureCommand();

    public static ConcurrentHashMap<UUID, Structure> playerLoadedStructures;

    public static EventNode<PlayerEvent> events() {
        EventNode<PlayerEvent> node = EventNode.type("player-structure-events", EventFilter.PLAYER);

        node.addListener(PlayerDisconnectEvent.class, event -> {
            playerLoadedStructures.remove(event.getPlayer().getUuid());
        });

        return node;
    }

    @Override
    public void preInitialize() {

    }

    @Override
    public void initialize() {
        System.out.println("MomStruct Extension initializing");

        playerLoadedStructures = new ConcurrentHashMap<>();

        getEventNode().addChild(events());

        MinecraftServer.getCommandManager().register(structureCommand);
    }

    @Override
    public void postInitialize() {

    }

    @Override
    public void terminate() {
        playerLoadedStructures.clear();
        MinecraftServer.getCommandManager().unregister(structureCommand);
    }
}
