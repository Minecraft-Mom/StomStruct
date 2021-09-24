package mom.minecraft.stomstruct.core.io;

import mom.minecraft.stomstruct.core.structure.Structure;
import net.minestom.server.instance.block.Block;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.File;
import java.io.IOException;

/**
 * Writes NBT structure files compatible with vanilla structure blocks.
 */
public class VanillaStructureWriter implements IStructureWriter{

    @Override
    public void write(Structure structure, String path) throws IOException {

        NBTList<NBTCompound> structureBlocks = new NBTList<>(NBTTypes.TAG_Compound);
        NBTList<NBTCompound> structureEntities = new NBTList<>(NBTTypes.TAG_Compound);
        NBTList<NBTList<NBTCompound>> structurePalettes = new NBTList<>(NBTTypes.TAG_List);
        NBTList<NBTInt> structureSize = new NBTList<>(NBTTypes.TAG_Int);
        NBTInt structureDataVersion = new NBTInt(-1);

        // Populate blocks list
        for (int i = 0; i < structure.getBlockCount(); i++)
        {
            // Add block state
            NBTInt blockState = new NBTInt(structure.getBlockStates()[i]);

            // Add block positions
            NBTList<NBTInt> blockPos = new NBTList<>(NBTTypes.TAG_Int);
            blockPos.add(new NBTInt(structure.getBlockPositions()[i][0]));
            blockPos.add(new NBTInt(structure.getBlockPositions()[i][1]));
            blockPos.add(new NBTInt(structure.getBlockPositions()[i][2]));

            NBTCompound block = new NBTCompound();
            block.set("pos", blockPos);
            block.set("state", blockState);

            // Optionally add block NBT
            if (structure.getBlockNBT()[i] != null) {
                NBTCompound nbt = structure.getBlockNBT()[i];
                block.set("nbt", nbt);
            }

            structureBlocks.add(block);
        }

        // Populate palette(s)
        for (int i = 0; i < structure.getPaletteCount(); i++) {
            NBTList<NBTCompound> palette = new NBTList<>(NBTTypes.TAG_Compound);

            for (int j = 0; j < structure.getBlockStatePalettes()[i].length; j++) {
                NBTCompound swatch = new NBTCompound();

                Block block = structure.getBlockStatePalettes()[0][j];
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
        structureSize.add(new NBTInt(structure.getWidth()));
        structureSize.add(new NBTInt(structure.getHeight()));
        structureSize.add(new NBTInt(structure.getLength()));

        // Create final NBT Compound for structure
        NBTCompound structureNBT = new NBTCompound();
        structureNBT.set("blocks", structureBlocks);
        structureNBT.set("entities", structureEntities);
        if (structure.getPaletteCount() == 1)
            structureNBT.set("palette", structurePalettes.get(0));
        else
            structureNBT.set("palettes", structurePalettes);
        structureNBT.set("size", structureSize);
        structureNBT.set("DataVersion", structureDataVersion);

        // Write NBT to file
        try (NBTWriter writer = new NBTWriter(new File(path), true)) {
            writer.writeNamed("", structureNBT);
        }
    }
}
