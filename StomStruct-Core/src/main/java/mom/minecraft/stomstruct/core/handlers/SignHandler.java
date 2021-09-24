package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SignHandler implements BlockHandler {
    Collection<Tag<?>> tags;

    public SignHandler() {
        tags = Arrays.asList(
                Tag.NBT("GlowingText"),
                Tag.NBT("Color"),
                Tag.NBT("Text1"),
                Tag.NBT("Text2"),
                Tag.NBT("Text3"),
                Tag.NBT("Text4")
        );
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return tags;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("stomstruct:sign_handler");
    }
}
