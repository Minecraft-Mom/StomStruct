package mom.minecraft.stomstruct.extension.commands;

import mom.minecraft.stomstruct.core.structure.Structure;
import mom.minecraft.stomstruct.core.io.StructureFormatException;
import mom.minecraft.stomstruct.extension.StomStructExtension;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeBlockPosition;
import net.minestom.server.entity.Player;

import java.io.IOException;

public class StructureCommand extends Command {
    public StructureCommand() {
        super("structure", "struct");

        ArgumentEnum<actions> actionArgument = ArgumentType.Enum("action", actions.class);
        actionArgument.setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentString pathArgument = ArgumentType.String("path");
        ArgumentRelativeBlockPosition pos1Argument = ArgumentType.RelativeBlockPosition("pos1");
        ArgumentRelativeBlockPosition pos2Argument = ArgumentType.RelativeBlockPosition("pos2");


        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("Structures directory: \"" + StomStructExtension.STRUCTURE_DIR + "\"");
            sender.sendMessage("/" + context.getCommandName() + " load <path>: Load a structure file to your clipboard");
            sender.sendMessage("/" + context.getCommandName() + " save: Save structure from your clipboard to file");
            sender.sendMessage("/" + context.getCommandName() + " build: Build structure from your clipboard");
            sender.sendMessage("/" + context.getCommandName() + " info: View details of structure from your clipboard");
            sender.sendMessage("/" + context.getCommandName() + " create <path> <pos1> <pos2>: New structure from instance");
        }));

        addSyntax(((sender, context) -> {
            Player player = sender.asPlayer();

            switch(context.get(actionArgument)) {
                case INFO:
                    if (StomStructExtension.playerLoadedStructures.containsKey(player.getUuid())) {
                        Structure structure = StomStructExtension.playerLoadedStructures.get(player.getUuid());
                        player.sendMessage("Structure Info:");
                        player.sendMessage("    Size: " + structure.getWidth() + ", " + structure.getHeight() + ", " + structure.getLength());
                        player.sendMessage("  Blocks: " + structure.getBlockCount());
                        player.sendMessage("Palettes: " + structure.getPaletteCount());
                    }
                    break;

                case BUILD:
                    if (StomStructExtension.playerLoadedStructures.containsKey(player.getUuid())) {
                        Structure structure = StomStructExtension.playerLoadedStructures.get(player.getUuid());
                        structure.build(player.getPosition(), player.getInstance());
                    }
                    break;

                case SAVE:
                case CREATE:
                case LOAD:
                    player.sendMessage("Invalid Command Syntax");
                    break;
            }
        }), actionArgument);

        addSyntax((sender, context) -> {
            Player player = sender.asPlayer();
            String path = context.get(pathArgument);

            switch(context.get(actionArgument)) {
                case LOAD:
                    try {
                        Structure structure = StomStructExtension.structureReader.read(StomStructExtension.STRUCTURE_DIR + path);
                        StomStructExtension.playerLoadedStructures.put(player.getUuid(), structure);
                    } catch (StructureFormatException | IOException e) {
                        player.sendMessage(e.getMessage());
                    }
                    break;

                case SAVE:
                    if (StomStructExtension.playerLoadedStructures.containsKey(player.getUuid())) {
                        Structure structure = StomStructExtension.playerLoadedStructures.get(player.getUuid());

                        try {
                            StomStructExtension.structureWriter.write(structure, StomStructExtension.STRUCTURE_DIR + path);
                            player.sendMessage("Structure written to disk");
                        } catch (IOException e) {
                            player.sendMessage(e.getMessage());
                        }
                    }
                    break;

                case INFO:
                case BUILD:
                case CREATE:
                    player.sendMessage("Invalid Command Syntax");
                    break;
            }
        }, actionArgument, pathArgument);

        addSyntax((sender, context) -> {
            Player player = sender.asPlayer();

            switch (context.get(actionArgument)) {
                case CREATE:
                    Structure s = new Structure(
                            context.get(pos1Argument).from(player),
                            context.get(pos2Argument).from(player),
                            player.getInstance());
                    StomStructExtension.playerLoadedStructures.put(player.getUuid(), s);
                    break;

                case INFO:
                case BUILD:
                case LOAD:
                    player.sendMessage("Invalid Command Syntax");
                    break;
            }
        }, actionArgument, pathArgument, pos1Argument, pos2Argument);
    }

    public enum actions {
        LOAD,
        SAVE,
        BUILD,
        INFO,
        CREATE
    }
}
