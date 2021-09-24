package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class EndGatewayHandler implements BlockHandler {
    Collection<Tag<?>> tags;

    public EndGatewayHandler() {
        tags = Arrays.asList(
                Tag.NBT("Age"),
                Tag.NBT("ExactTeleport"),
                Tag.NBT("ExitPortal")
        );
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return tags;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("stomstruct:end_gateway_handler");
    }
}
