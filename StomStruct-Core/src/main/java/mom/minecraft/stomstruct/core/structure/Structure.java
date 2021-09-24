package mom.minecraft.stomstruct.core.structure;

import mom.minecraft.stomstruct.core.handlers.BlockHandlers;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import java.util.HashMap;

/**
 * Represents a collection of blocks contained within a cuboid region of space.
 * Structures can be created from a region of space in-game or read in from external data stores.
 * They can contain one or many block palettes, allowing efficient "re-skinning" of structures.
 * In vanilla these palettes are used for different types of shipwrecks.
 *
 */
public class Structure {

    private final int blockCount;
    private final short[][] blockPositions;
    private final short[] blockStates;
    private final NBTCompound[] blockNBT;

    private final int paletteCount;
    private final Block[][] blockStatePalettes;

    private final int width; // X
    private final int height; // Y
    private final int length; // Z

    /** TODO
     * Constructs structure by copying all the required fields
     * @param blockPositions Array of 3-Int arrays providing X, Y, Z coordinates for each block
     * @param blockStates
     * @param blockNBT
     * @param blockStatePalettes
     * @param width
     * @param height
     * @param length
     */
    public Structure(short[][] blockPositions, short[] blockStates, NBTCompound[] blockNBT, Block[][] blockStatePalettes, int width, int height, int length) {
        this.blockPositions = blockPositions;
        this.blockStates = blockStates;
        this.blockNBT = blockNBT;
        this.blockStatePalettes = blockStatePalettes;
        this.width = width;
        this.height = height;
        this.length = length;

        paletteCount = blockStatePalettes.length;
        blockCount = blockPositions.length;
    }

    public Structure(@NotNull Point pos1, @NotNull Point pos2, @NotNull Instance instance) {
        // Keep track of corner with lowest X,Y,Z values
        Point truePos1 = new Pos(
                Math.min(pos1.blockX(), pos2.blockX()),
                Math.min(pos1.blockY(), pos2.blockY()),
                Math.min(pos1.blockZ(), pos2.blockZ()));

        // Keep track of corner with highest X,Y,Z values
        Point truePos2 = new Pos(
                Math.max(pos1.blockX(), pos2.blockX()),
                Math.max(pos1.blockY(), pos2.blockY()),
                Math.max(pos1.blockZ(), pos2.blockZ()));

        width = truePos2.blockX() - truePos1.blockX() + 1;
        height = truePos2.blockY() - truePos1.blockY() + 1;
        length = truePos2.blockZ() - truePos1.blockZ() + 1;
        blockCount = width * length * height;

        paletteCount = 1;

        blockPositions = new short[blockCount][3];
        blockStates = new short[blockCount];
        blockStatePalettes = new Block[1][];
        blockNBT = new NBTCompound[blockCount];

        HashMap<String, Block> paletteBlockMap = new HashMap<>();
        HashMap<String, Short> paletteIdMap = new HashMap<>();

        // Process Each Block in the region
        int blockIndex = 0;
        short blockStateIndex = -1;
        for (short y = 0; y < height; y++)
            for (short x = 0; x < width; x++)
                for (short z = 0; z < length; z++) {
                    Block block = instance.getBlock(truePos1.add(x, y, z));
                    Block blockNBTStripped = block.withNbt(new NBTCompound());

                    String blockKey = blockNBTStripped.toString();
                    if (!paletteIdMap.containsKey(blockKey))
                    {
                        paletteIdMap.put(blockKey, ++blockStateIndex);
                        paletteBlockMap.put(blockKey, blockNBTStripped);
                    }

                    blockPositions[blockIndex][0] = x;
                    blockPositions[blockIndex][1] = y;
                    blockPositions[blockIndex][2] = z;
                    blockStates[blockIndex] = paletteIdMap.get(blockKey);
                    blockNBT[blockIndex] = block.nbt();

                    blockIndex++;
                }

        // Build palettes array from unique blockstates
        blockStatePalettes[0] = new Block[paletteBlockMap.size()];
        for (String key : paletteIdMap.keySet()) {
            blockStatePalettes[0][paletteIdMap.get(key)] = paletteBlockMap.get(key);
        }
    }

    public void build(@NotNull Point origin, @NotNull Instance instance) {
        buildWithPalette(origin, instance, 0);
    }

    public void buildWithPalette(@NotNull Point origin, @NotNull Instance instance, int paletteIndex) {
        RelativeBlockBatch blockBatch = new RelativeBlockBatch();

        for (int i = 0; i < blockCount; i++)
        {
            Block b = blockStatePalettes[paletteIndex][blockStates[i]];

            if (b.compare(Block.STRUCTURE_VOID, Block.Comparator.ID))
                continue;

            if (blockNBT[i] != null)
                b = b.withNbt(blockNBT[i]).withHandler(BlockHandlers.fromBlockName(blockStatePalettes[paletteIndex][blockStates[i]].name()));

            blockBatch.setBlock(
                    blockPositions[i][0],
                    blockPositions[i][1],
                    blockPositions[i][2],
                    b);
        }

        blockBatch.apply(instance, origin, null);
    }

    /**
     * Converts the structure into a Relative Block Batch
     */
    public RelativeBlockBatch asRelativeBlockBatch() {
        return asRelativeBlockBatch(0);
    }

    /**
     * Converts the structure into a Relative Block Batch
     * @param paletteIndex Structure palette to use
     */
    public RelativeBlockBatch asRelativeBlockBatch(int paletteIndex) {
        RelativeBlockBatch blockBatch = new RelativeBlockBatch();

        for (int i = 0; i < blockCount; i++)
        {
            Block b = blockStatePalettes[paletteIndex][blockStates[i]];

            if (b.compare(Block.STRUCTURE_VOID, Block.Comparator.ID))
                continue;

            if (blockNBT[i] != null)
                b = b.withNbt(blockNBT[i]).withHandler(BlockHandlers.fromBlockName(blockStatePalettes[paletteIndex][blockStates[i]].name()));

            blockBatch.setBlock(
                    blockPositions[i][0],
                    blockPositions[i][1],
                    blockPositions[i][2],
                    b);
        }

        return blockBatch;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public short[][] getBlockPositions() {
        return blockPositions;
    }

    public short[] getBlockStates() {
        return blockStates;
    }

    public NBTCompound[] getBlockNBT() {
        return blockNBT;
    }

    public int getPaletteCount() {
        return paletteCount;
    }

    public Block[][] getBlockStatePalettes() {
        return blockStatePalettes;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }
}

