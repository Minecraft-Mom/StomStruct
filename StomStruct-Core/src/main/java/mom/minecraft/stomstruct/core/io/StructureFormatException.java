package mom.minecraft.stomstruct.core.io;

/**
 * Exception to be thrown by a Structure Reader when the data being read is not valid.
 */
public class StructureFormatException extends Exception{
    public StructureFormatException(String message) {
        super (message);
    }
}
