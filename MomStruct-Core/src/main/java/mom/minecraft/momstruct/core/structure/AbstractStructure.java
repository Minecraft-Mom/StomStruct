package mom.minecraft.momstruct.core.structure;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractStructure {
    abstract Status read();
    abstract Status write();
    abstract Status build(@NotNull Point origin, @NotNull Instance instance);

    AbstractStructure(){
    };

    public enum Status {
        LOADED,
        NOT_LOADED,
        NO_SUCH_FILE,
        BAD_READ,
        BAD_WRITE,
        BAD_NBT
    }
}
