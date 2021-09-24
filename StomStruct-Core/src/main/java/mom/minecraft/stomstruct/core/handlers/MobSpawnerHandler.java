package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class MobSpawnerHandler implements BlockHandler {
    Collection<Tag<?>> tags;

    public MobSpawnerHandler() {
        tags = Arrays.asList(
                Tag.NBT("Delay"),
                Tag.NBT("MaxNearbyEntities"),
                Tag.NBT("MaxSpawnDelay"),
                Tag.NBT("MinSpawnDelay"),
                Tag.NBT("RequiredPlayerRange"),
                Tag.NBT("SpawnCount"),
                Tag.NBT("SpawnData"),
                Tag.NBT("SpawnPotentials"),
                Tag.NBT("SpawnRange")
        );
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return tags;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("stomstruct:mob_spawner_handler");
    }
}
