package mom.minecraft.stomstruct.core.io;

import mom.minecraft.stomstruct.core.structure.Structure;

import java.io.IOException;

public interface IStructureWriter {
    void write(Structure structure, String path) throws IOException;
}
