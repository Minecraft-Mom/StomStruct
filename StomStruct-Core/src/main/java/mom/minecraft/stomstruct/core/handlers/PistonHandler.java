package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class PistonHandler implements BlockHandler {
    Collection<Tag<?>> tags;

    public PistonHandler() {
        tags = Arrays.asList(
                Tag.NBT("blockState"),
                Tag.NBT("extending"),
                Tag.NBT("facing"),
                Tag.NBT("progress"),
                Tag.NBT("source")
        );
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return tags;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("stomstruct:piston_handler");
    }
}
