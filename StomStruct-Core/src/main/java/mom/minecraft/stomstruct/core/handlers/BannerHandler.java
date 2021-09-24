package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class BannerHandler implements BlockHandler {
    Collection<Tag<?>> tags;

    public BannerHandler() {
        tags = Arrays.asList(
                Tag.NBT("Patterns")
        );
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {

        return tags;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("stomstruct:banner_handler");
    }
}
