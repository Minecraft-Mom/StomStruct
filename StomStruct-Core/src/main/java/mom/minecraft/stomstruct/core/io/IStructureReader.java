package mom.minecraft.stomstruct.core.io;

import mom.minecraft.stomstruct.core.structure.Structure;
import mom.minecraft.stomstruct.core.structure.StructureFormatException;

import java.io.IOException;

public interface IStructureReader {
    Structure read(String path) throws StructureFormatException, IOException;
}
