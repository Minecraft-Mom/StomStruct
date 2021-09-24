package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public class DummyHandler implements BlockHandler {
    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("stomstruct:dummy_handler");
    }
}
