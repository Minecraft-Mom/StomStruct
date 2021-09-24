package mom.minecraft.stomstruct.core.io;

import mom.minecraft.stomstruct.core.structure.Structure;

import java.io.IOException;

/**
 * Represents an element that can write a structure from memory into some data store/format.
 */
public interface IStructureWriter {

    /**
     *
     * @param structure Structure to output
     * @param key A unique string identifying structure to write. Could be a path or unique ID.
     * @throws IOException
     */
    void write(Structure structure, String key) throws IOException;
}
