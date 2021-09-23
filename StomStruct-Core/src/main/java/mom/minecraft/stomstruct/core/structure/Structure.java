package mom.minecraft.stomstruct.core.structure;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Structure {

    // Structure Data
    private int blockCount;
    private int[][] blockPositions;
    private int[] blockStates;
    private NBTCompound[] blockNBT;

    private int paletteCount;
    private Block[][] blockStatePalettes;

    private int width; // X
    private int height; // Y
    private int length; // Z

    public Structure(int[][] blockPositions, int[] blockStates, NBTCompound[] blockNBT, Block[][] blockStatePalettes, int width, int height, int length) {
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

        blockPositions = new int[blockCount][3];
        blockStates = new int[blockCount];
        blockStatePalettes = new Block[1][];
        blockNBT = new NBTCompound[blockCount];

        HashMap<String, Block> paletteBlockMap = new HashMap<>();
        HashMap<String, Integer> paletteIdMap = new HashMap<>();

        // Process Each Block in the region
        int blockIndex = 0;
        int blockStateIndex = -1;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                for (int z = 0; z < length; z++) {
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
            if (blockNBT[i] == null)
            {
                blockBatch.setBlock(
                        blockPositions[i][0],
                        blockPositions[i][1],
                        blockPositions[i][2],
                        blockStatePalettes[paletteIndex][blockStates[i]]);
                System.out.println(blockStatePalettes[paletteIndex][blockStates[i]]);
            } else {
                blockBatch.setBlock(
                        blockPositions[i][0],
                        blockPositions[i][1],
                        blockPositions[i][2],
                        blockStatePalettes[paletteIndex][blockStates[i]].withNbt(blockNBT[i]));
                System.out.println(blockStatePalettes[paletteIndex][blockStates[i]].withNbt(blockNBT[i]));
            }
        }

        blockBatch.apply(instance, origin, null);
    }

    public RelativeBlockBatch asRelativeBlockBatch() {
        return asRelativeBlockBatch(0);
    }

    public RelativeBlockBatch asRelativeBlockBatch(int paletteIndex) {
        RelativeBlockBatch blockBatch = new RelativeBlockBatch();
        for (int i = 0; i < blockCount; i++)
        {
            if (blockNBT[i] == null)
            {
                blockBatch.setBlock(
                        blockPositions[i][0],
                        blockPositions[i][1],
                        blockPositions[i][2],
                        blockStatePalettes[paletteIndex][blockStates[i]]);
                System.out.println(blockStatePalettes[paletteIndex][blockStates[i]]);
            } else {
                blockBatch.setBlock(
                        blockPositions[i][0],
                        blockPositions[i][1],
                        blockPositions[i][2],
                        blockStatePalettes[paletteIndex][blockStates[i]].withNbt(blockNBT[i]));
                System.out.println(blockStatePalettes[paletteIndex][blockStates[i]].withNbt(blockNBT[i]));
            }
        }

        return blockBatch;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public int[][] getBlockPositions() {
        return blockPositions;
    }

    public int[] getBlockStates() {
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

