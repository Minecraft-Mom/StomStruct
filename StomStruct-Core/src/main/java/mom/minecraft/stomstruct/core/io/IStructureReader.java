package mom.minecraft.stomstruct.core.io;

import mom.minecraft.stomstruct.core.structure.Structure;

import java.io.IOException;

/**
 * Represents an element that can read a structure into memory from some data store/format.
 */
public interface IStructureReader {

    /**
     * Read a structure from some source.
     * @param key A unique string identifying structure to read. Could be a path or unique ID.
     * @return Structure
     * @throws StructureFormatException
     * @throws IOException
     */
    Structure read(String key) throws StructureFormatException, IOException;
}
