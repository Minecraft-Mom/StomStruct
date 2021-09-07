package mom.minecraft.momstruct.core.structure;

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

public class VanillaStructure extends AbstractStructure {
    public static int LATEST_DATA_VERSION = 2730;
    public static String STRUCTURE_DIRECTORY = "./structures/";

    private final String structureFilePath;
    private Status status;

    // Structure Data
    private int dataVersion;

    private int blockCount;
    private int[][] blockPositions;
    private int[] blockStates;
    private NBTCompound[] blockNBT;

    private int paletteCount;
    private Block[][] blockStatePalettes;

    private int width; // X
    private int height; // Y
    private int length; // Z

    public VanillaStructure(@NotNull String structureFilePath) {
        super();
        this.structureFilePath = structureFilePath;
        status = Status.NOT_LOADED;
    }

    public VanillaStructure(@NotNull Point pos1, @NotNull Point pos2, @NotNull Instance instance, String structureFilePath) {
        super();

        this.structureFilePath = structureFilePath;
        status = Status.NOT_LOADED;

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
        dataVersion = LATEST_DATA_VERSION;

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

        status = Status.LOADED;
    }

    @Override
    public Status read() {
        // Make sure file isn't already loaded
        if (status == Status.LOADED)
            return status;

        // Check that the schematic file is indeed a real file
        File schematicFile = new File(STRUCTURE_DIRECTORY + structureFilePath);
        if (!schematicFile.exists() || !schematicFile.isFile()) {
            status = Status.NO_SUCH_FILE;
            return status;
        }

        // Create NBT Reader and process the supplied structure file
        try (NBTReader reader = new NBTReader(schematicFile, true)) {

            // Read in all NBT from the structure file
            NBTCompound structureNBT = (NBTCompound) reader.read();

            NBTList<NBTInt> structureSize = structureNBT.getList("size");
            dataVersion = structureNBT.getInt("DataVersion");
            NBTList<NBTCompound> structureBlocks = structureNBT.getList("blocks");
            NBTList<NBTCompound> structureEntities = structureNBT.getList("entities");

            // There can be one "palette" or multiple under "palettes"
            NBTList<NBTList<NBTCompound>> structurePalettes;
            if (structureNBT.containsKey("palettes")) {
                structurePalettes = structureNBT.getList("palettes");
            } else if (structureNBT.containsKey("palette")) {
                structurePalettes = new NBTList<>(NBTTypes.TAG_List);
                structurePalettes.add(structureNBT.getList("palette"));
            } else {
                status = Status.BAD_NBT;
                return status;
            }

            blockCount = structureBlocks.getLength();
            paletteCount = structurePalettes.getLength();

            width = structureSize.get(0).getValue();
            height = structureSize.get(1).getValue();
            length = structureSize.get(2).getValue();

            // Initialize arrays for structure data
            blockPositions = new int[blockCount][3];
            blockStates = new int[blockCount];
            blockStatePalettes = new Block[paletteCount][];
            blockNBT = new NBTCompound[blockCount];

            // Load in all structure block data
            for (short i = 0; i < blockCount; i++)
            {
                NBTCompound structureBlock = structureBlocks.get(i);
                NBTList<NBTInt> pos = structureBlock.getList("pos");
                blockPositions[i] = new int[3];
                for (int j = 0; j < 3; j++)
                {
                    blockPositions[i][j] = pos.get(j).getValue();
                }
                blockStates[i] = structureBlock.getInt("state");

                if (structureBlock.containsKey("nbt"))
                    blockNBT[i] = structureBlock.getCompound("nbt");
            }

            // Load in all palette block data
            Map<String, String> tempPropsMap = new HashMap<>();
            for (int i = 0; i < paletteCount; i++) {
                NBTList<NBTCompound> nbtPalette = structurePalettes.get(i);
                blockStatePalettes[i] = new Block[nbtPalette.getLength()];

                for (int j = 0; j < nbtPalette.getLength(); j++) {
                    NBTCompound swatch = nbtPalette.get(j);

                    if (swatch.containsKey("Properties"))
                    {
                        NBTCompound props = swatch.getCompound("Properties");

                        if (!tempPropsMap.isEmpty())
                            tempPropsMap.clear();

                        for (String key : props.getKeys()) {
                            tempPropsMap.put(key, props.getString(key));
                        }

                        blockStatePalettes[i][j] = Block.fromNamespaceId(swatch.getString("Name")).withProperties(tempPropsMap);
                    } else {
                        blockStatePalettes[i][j] = Block.fromNamespaceId(swatch.getString("Name"));
                    }
                }
            }
        } catch (NullPointerException | NBTException ex) {
            ex.printStackTrace();
            status = Status.BAD_NBT;
            return status;
        } catch (IOException ex) {
            ex.printStackTrace();
            status = Status.BAD_READ;
            return status;
        }

        status = Status.LOADED;
        return status;
    }

    @Override
    public Status write() {
        NBTList<NBTCompound> structureBlocks = new NBTList<>(NBTTypes.TAG_Compound);
        NBTList<NBTCompound> structureEntities = new NBTList<>(NBTTypes.TAG_Compound);
        NBTList<NBTList<NBTCompound>> structurePalettes = new NBTList<>(NBTTypes.TAG_List);
        NBTList<NBTInt> structureSize = new NBTList<>(NBTTypes.TAG_Int);
        NBTInt structureDataVersion = new NBTInt(dataVersion);

        // Populate blocks list
        for (int i = 0; i < blockCount; i++)
        {
            // Add block state
            NBTInt blockState = new NBTInt(blockStates[i]);

            // Add block positions
            NBTList<NBTInt> blockPos = new NBTList<>(NBTTypes.TAG_Int);
            blockPos.add(new NBTInt(blockPositions[i][0]));
            blockPos.add(new NBTInt(blockPositions[i][1]));
            blockPos.add(new NBTInt(blockPositions[i][2]));

            NBTCompound block = new NBTCompound();
            block.set("pos", blockPos);
            block.set("state", blockState);

            // Optionally add block NBT
            if (blockNBT[i] != null) {
                NBTCompound nbt = blockNBT[i];
                block.set("nbt", nbt);
            }

            structureBlocks.add(block);
        }

        // Populate palette(s)
        for (int i = 0; i < paletteCount; i++) {
            NBTList<NBTCompound> palette = new NBTList<>(NBTTypes.TAG_Compound);

            for (int j = 0; j < blockStatePalettes[i].length; j++) {
                NBTCompound swatch = new NBTCompound();

                Block block = blockStatePalettes[0][j];
                swatch.set("Name", new NBTString(block.name()));

                if (!block.properties().isEmpty()) {
                    NBTCompound swatchProperties = new NBTCompound();
                    for (String key: block.properties().keySet()) {
                        swatchProperties.set(key, new NBTString(block.getProperty(key)));
                    }

                    swatch.set("Properties", swatchProperties);
                }

                palette.add(swatch);
            }
            structurePalettes.add(palette);
        }

        // Populate size
        structureSize.add(new NBTInt(width));
        structureSize.add(new NBTInt(height));
        structureSize.add(new NBTInt(length));

        // Create final NBT Compound for structure
        NBTCompound structure = new NBTCompound();
        structure.set("blocks", structureBlocks);
        structure.set("entities", structureEntities);
        if (paletteCount == 1)
            structure.set("palette", structurePalettes.get(0));
        else
            structure.set("palettes", structurePalettes);
        structure.set("size", structureSize);
        structure.set("DataVersion", structureDataVersion);

        // Write NBT to file
        try (NBTWriter writer = new NBTWriter(new File(STRUCTURE_DIRECTORY + structureFilePath), true)) {
            writer.writeNamed("", structure);
        } catch (IOException ex) {
            ex.printStackTrace(); // TODO remove
            status = Status.BAD_WRITE;
        }
        return null;
    }

    @Override
    public Status build(@NotNull Point origin, @NotNull Instance instance) {
        return buildWithPalette(origin, instance, 0);
    }

    public Status buildWithPalette(@NotNull Point origin, @NotNull Instance instance, int paletteIndex) {
        if (status != Status.LOADED)
            return status;

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

        return status;
    }

    public String getStructureFilePath() {
        return structureFilePath;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public int getPaletteCount() {
        return paletteCount;
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

    public int getDataVersion() {
        return dataVersion;
    }

    public Status getStatus() {
        return status;
    }
}
