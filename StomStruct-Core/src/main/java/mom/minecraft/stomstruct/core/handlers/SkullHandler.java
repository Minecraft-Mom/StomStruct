package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class SkullHandler implements BlockHandler {
    Collection<Tag<?>> tags;

    public SkullHandler() {
        tags = Arrays.asList(
                Tag.NBT("ExtraType"),
                Tag.NBT("SkullOwner")
        );
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return tags;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("stomstruct:skull_handler");
    }
}
